package com.example.idp;

import jakarta.security.auth.message.AuthException;
import jakarta.security.auth.message.AuthStatus;
import jakarta.security.auth.message.MessageInfo;
import jakarta.security.auth.message.MessagePolicy;
import jakarta.security.auth.message.callback.CallerPrincipalCallback;
import jakarta.security.auth.message.callback.GroupPrincipalCallback;
import jakarta.security.auth.message.module.ServerAuthModule;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jboss.logging.Logger;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

/**
 * <h2>Rôle de la classe</h2>
 * <p>Implémentation principale du module JASPIC chargé de gérer le cycle complet d'authentification contre un IdP.
 * Il orchestre la redirection, la validation de réponse et la propagation du principal.</p>
 *
 * <h3>Responsabilités</h3>
 * <ul>
 *     <li>Initialiser les composants de validation et de construction de requête.</li>
 *     <li>Déterminer si l'utilisateur est déjà authentifié.</li>
 *     <li>Rediriger vers l'IdP si nécessaire.</li>
 *     <li>Valider la réponse IdP et peupler le {@link Subject}.</li>
 * </ul>
 */
public class IdpServerAuthModule implements ServerAuthModule {

    private static final Class<?>[] SUPPORTED_MESSAGE_TYPES = new Class<?>[]{HttpServletRequest.class, HttpServletResponse.class};

    private CallbackHandler callbackHandler;
    private ConfigProperties configProperties;
    private IdpResponseValidator responseValidator;
    private IdpAuthRequestBuilder authRequestBuilder;
    private SessionManager sessionManager;
    private Logger logger;

    /**
     * Initialise le module avec les politiques JASPIC et la configuration WildFly.
     *
     * @param requestPolicy  politique requête (non utilisée ici)
     * @param responsePolicy politique réponse (non utilisée ici)
     * @param serverSubject  sujet serveur (non utilisé)
     * @param options        options fournies par le conteneur (précondition : non null)
     * @throws AuthException en cas d'erreur de configuration
     */
    @Override
    public void initialize(final MessagePolicy requestPolicy, final MessagePolicy responsePolicy, final CallbackHandler handler,
                          final Map<String, Object> options) throws AuthException {
        this.callbackHandler = handler;
        if (this.callbackHandler == null) {
            throw new AuthException("CallbackHandler not provided in options");
        }
        this.configProperties = new ConfigProperties(options.entrySet().stream()
                .collect(java.util.stream.Collectors.toMap(Map.Entry::getKey, e -> String.valueOf(e.getValue()))));
        this.responseValidator = new IdpResponseValidator(createDefaultTokenValidator());
        this.authRequestBuilder = new IdpAuthRequestBuilder(configProperties, createDefaultIdpClient());
        this.sessionManager = new SessionManager();
        this.logger = LoggerUtils.getLogger(IdpServerAuthModule.class);
    }

    private IdpClient createDefaultIdpClient() {
        return requestContext -> Map.of(
                "client_id", configProperties.getClientId(),
                "redirect_uri", configProperties.getRedirectUri(),
                "response_type", "code",
                "scope", "openid profile"
        );
    }

    private TokenValidator createDefaultTokenValidator() {
        return (token, requestUri) -> new IdpUserInfo(token, Set.of("user"));
    }

    @Override
    public Class<?>[] getSupportedMessageTypes() {
        return Arrays.copyOf(SUPPORTED_MESSAGE_TYPES, SUPPORTED_MESSAGE_TYPES.length);
    }

    /**
     * Gère la requête entrante.
     *
     * @param messageInfo    enveloppe contenant requête/réponse
     * @param clientSubject  sujet utilisateur à enrichir
     * @param serviceSubject sujet serveur
     * @return statut d'authentification JASPIC
     * @throws AuthException en cas d'échec d'authentification ou de redirection
     */
    @Override
    public AuthStatus validateRequest(final MessageInfo messageInfo, final Subject clientSubject, final Subject serviceSubject) throws AuthException {
        final HttpServletRequest request = (HttpServletRequest) messageInfo.getRequestMessage();
        final HttpServletResponse response = (HttpServletResponse) messageInfo.getResponseMessage();

        try {
            final var existing = sessionManager.retrieve(request);
            if (existing.isPresent()) {
                logger.debug("User already authenticated in session");
                establishIdentity(clientSubject, existing.get());
                return AuthStatus.SUCCESS;
            }

            if (isResponseFromIdp(request)) {
                logger.debug("Validating IdP response");
                final IdpUserInfo userInfo = responseValidator.validateResponse(request);
                sessionManager.store(request, userInfo);
                establishIdentity(clientSubject, userInfo);
                return AuthStatus.SUCCESS;
            }

            logger.debug("Redirecting to IdP");
            final String redirectUrl = authRequestBuilder.buildRedirectUrl(request);
            response.sendRedirect(redirectUrl);
            messageInfo.getMap().put("jakarta.security.auth.message.MessageInfo.isMandatory", Boolean.TRUE);
            return AuthStatus.SEND_CONTINUE;
        } catch (IdpValidationException | IdpConfigurationException | IdpCommunicationException e) {
            logger.error("Authentication failed", e);
            throw new AuthException(e.getMessage());
        } catch (IOException e) {
            logger.error("Unable to redirect to IdP", e);
            throw new AuthException(e.getMessage());
        }
    }

    private boolean isResponseFromIdp(final HttpServletRequest request) {
        return request.getParameter("SAMLResponse") != null || request.getParameter("id_token") != null;
    }

    private void establishIdentity(final Subject clientSubject, final IdpUserInfo userInfo) throws AuthException {
        final IdpPrincipal principal = new IdpPrincipal(userInfo.getSubject(), userInfo.getRoles());
        final Callback[] callbacks = new Callback[]{
                new CallerPrincipalCallback(clientSubject, principal),
                new GroupPrincipalCallback(clientSubject, userInfo.getRoles().toArray(new String[0]))
        };
        try {
            callbackHandler.handle(callbacks);
        } catch (IOException | UnsupportedCallbackException e) {
            throw new AuthException("Failed to propagate principal: " + e.getMessage());
        }
    }

    @Override
    public AuthStatus secureResponse(final MessageInfo messageInfo, final Subject serviceSubject) {
        return AuthStatus.SEND_SUCCESS;
    }

    @Override
    public void cleanSubject(final MessageInfo messageInfo, final Subject subject) {
        final HttpServletRequest request = (HttpServletRequest) messageInfo.getRequestMessage();
        sessionManager.invalidate(request);
    }
}
