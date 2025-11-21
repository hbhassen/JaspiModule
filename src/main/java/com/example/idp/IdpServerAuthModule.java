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

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;

/**
 * <p>IdpServerAuthModule implements the JASPIC contract and orchestrates the full authentication lifecycle.</p>
 * <p>Responsibilities:</p>
 * <ul>
 *     <li>Drive challenge/response exchanges with the Identity Provider.</li>
 *     <li>Translate validated IdP responses into container-recognized principals.</li>
 *     <li>Propagate roles to WildFly using standard callbacks.</li>
 * </ul>
 * <p>Usage example:</p>
 * <pre>{@code
 * IdpServerAuthModule module = new IdpServerAuthModule();
 * module.initialize(null, null, callbackHandler, Map.of(
 *     ConfigProperties.IDP_AUTH_ENDPOINT, "https://idp.example.com/auth",
 *     ConfigProperties.CLIENT_ID, "wildfly-module",
 *     ConfigProperties.REDIRECT_URI, "https://app.example.com/callback"
 * ));
 * AuthStatus status = module.validateRequest(messageInfo, clientSubject, serviceSubject);
 * }</pre>
 * <p>Interaction diagram:</p>
 * <ol>
 *     <li>WildFly invokes {@link #validateRequest(MessageInfo, Subject, Subject)} for each request.</li>
 *     <li>The method checks existing session state via {@link SessionManager}.</li>
 *     <li>If an IdP response is present, {@link IdpResponseValidator} is called to build {@link IdpPrincipal}.</li>
 *     <li>If no user is authenticated, {@link IdpAuthRequestBuilder} provides the redirect URL and {@link AuthStatus#SEND_CONTINUE} is returned.</li>
 * </ol>
 */
public class IdpServerAuthModule implements ServerAuthModule {

    private static final Class<?>[] SUPPORTED_TYPES = new Class[]{HttpServletRequest.class, HttpServletResponse.class};

    private final Logger logger = LoggerUtils.getLogger(IdpServerAuthModule.class);

    private CallbackHandler callbackHandler;
    private ConfigProperties configProperties;
    private IdpAuthRequestBuilder authRequestBuilder;
    private IdpResponseValidator responseValidator;
    private SessionManager sessionManager;

    @Override
    public void initialize(MessagePolicy requestPolicy, MessagePolicy responsePolicy, CallbackHandler handler, Map options) throws AuthException {
        this.callbackHandler = handler;
        Map<String, String> stringOptions = options == null ? Collections.emptyMap() : options;
        this.configProperties = new ConfigProperties(stringOptions);
        this.configProperties.validate();
        this.authRequestBuilder = new IdpAuthRequestBuilder(configProperties);
        this.sessionManager = new SessionManager();
        IdpClient client = payload -> Map.of("token", payload, "sub", payload); // placeholder non-network exchange
        TokenValidator tokenValidator = (attributes, roleClaim) -> IdpUserInfo.builder(attributes.getOrDefault("sub", "anonymous"))
            .addRole(attributes.getOrDefault(roleClaim, "user"))
            .build();
        this.responseValidator = new IdpResponseValidator(client, tokenValidator, configProperties);
        logger.info("IdpServerAuthModule initialized with endpoints: " + configProperties.getAuthEndpoint());
    }

    @Override
    public Class<?>[] getSupportedMessageTypes() {
        return SUPPORTED_TYPES.clone();
    }

    @Override
    public AuthStatus validateRequest(MessageInfo messageInfo, Subject clientSubject, Subject serviceSubject) throws AuthException {
        HttpServletRequest request = (HttpServletRequest) messageInfo.getRequestMessage();
        HttpServletResponse response = (HttpServletResponse) messageInfo.getResponseMessage();

        IdpPrincipal existing = sessionManager.getPrincipal(request);
        if (existing != null) {
            logger.debug("User already authenticated; skipping IdP redirect");
            establishCaller(clientSubject, existing);
            return AuthStatus.SUCCESS;
        }

        String samlResponse = request.getParameter("SAMLResponse");
        String token = request.getParameter("id_token");
        if ((samlResponse != null && !samlResponse.isBlank()) || (token != null && !token.isBlank())) {
            IdpUserInfo userInfo = responseValidator.validateResponse(samlResponse, token);
            IdpPrincipal principal = toPrincipal(userInfo);
            sessionManager.storePrincipal(request, principal);
            establishCaller(clientSubject, principal);
            return AuthStatus.SUCCESS;
        }

        return sendAuthRedirect(response, messageInfo);
    }

    @Override
    public AuthStatus secureResponse(MessageInfo messageInfo, Subject serviceSubject) throws AuthException {
        return AuthStatus.SEND_SUCCESS;
    }

    @Override
    public void cleanSubject(MessageInfo messageInfo, Subject subject) throws AuthException {
        if (messageInfo.getRequestMessage() instanceof HttpServletRequest request) {
            sessionManager.clearPrincipal(request);
        }
        if (subject != null) {
            subject.getPrincipals().clear();
        }
    }

    private AuthStatus sendAuthRedirect(HttpServletResponse response, MessageInfo messageInfo) throws AuthException {
        String state = UUID.randomUUID().toString();
        String redirectUrl = authRequestBuilder.buildAuthRequest(state);
        try {
            response.sendRedirect(redirectUrl);
            messageInfo.getMap().put("javax.servlet.http.registerSession", Boolean.TRUE.toString());
            logger.debugf("Issued IdP redirect to %s", redirectUrl);
            return AuthStatus.SEND_CONTINUE;
        } catch (IOException e) {
            throw new AuthException("Unable to send redirect: " + e.getMessage());
        }
    }

    private void establishCaller(Subject clientSubject, IdpPrincipal principal) throws AuthException {
        try {
            callbackHandler.handle(new javax.security.auth.callback.Callback[]{
                new CallerPrincipalCallback(clientSubject, principal),
                new GroupPrincipalCallback(clientSubject, principal.getRoles().toArray(new String[0]))
            });
        } catch (Exception e) {
            throw new AuthException("Failed to propagate principal: " + e.getMessage());
        }
    }

    private IdpPrincipal toPrincipal(IdpUserInfo userInfo) {
        IdpPrincipal principal = new IdpPrincipal(userInfo.getName(), userInfo.getRoles());
        principal.getAttributes().putAll(userInfo.getAttributes());
        return principal;
    }
}
