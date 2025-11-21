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

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;

/**
 * Core JASPIC implementation integrating IdP authentication within WildFly.
 * <p>Responsibilities:</p>
 * <ul>
 *     <li>Drive the JASPIC lifecycle and orchestrate IdP redirects/validations.</li>
 *     <li>Populate {@link javax.security.auth.Subject} with {@link IdpPrincipal} and roles.</li>
 *     <li>Maintain session continuity through {@link SessionManager}.</li>
 * </ul>
 * Usage example in WildFly login-config.xml:
 * <pre>
 * {@code
 * <auth-module code="com.example.idp.IdpServerAuthModule" module="com.example.idp"/>
 * }
 * </pre>
 */
public class IdpServerAuthModule implements ServerAuthModule {

    private static final Class<?>[] SUPPORTED_MESSAGE_TYPES = new Class<?>[]{
            HttpServletRequest.class,
            HttpServletResponse.class
    };

    private CallbackHandler callbackHandler;
    private ConfigProperties config;
    private IdpAuthRequestBuilder authRequestBuilder;
    private IdpResponseValidator responseValidator;
    private SessionManager sessionManager;

    /**
     * Default constructor relying on configuration provided at runtime.
     */
    public IdpServerAuthModule() {
    }

    @Override
    public void initialize(MessagePolicy requestPolicy, MessagePolicy responsePolicy, CallbackHandler handler, @SuppressWarnings("rawtypes") Map options) throws AuthException {
        this.callbackHandler = handler;
        Map<String, Object> safeOptions = options == null ? Map.of() : Map.copyOf(options);
        this.config = ConfigProperties.fromOptions(safeOptions);
        this.authRequestBuilder = new IdpAuthRequestBuilder(config);
        // Default token validator echoes the token as username; replace in production via a custom validator.
        this.responseValidator = new IdpResponseValidator(token -> IdpUserInfo.builder().username(token).addRole("user").build());
        this.sessionManager = new SessionManager();
    }

    @Override
    public Class<?>[] getSupportedMessageTypes() {
        return SUPPORTED_MESSAGE_TYPES;
    }

    @Override
    public AuthStatus validateRequest(MessageInfo messageInfo, Subject clientSubject, Subject serviceSubject) throws AuthException {
        HttpServletRequest request = (HttpServletRequest) messageInfo.getRequestMessage();
        HttpServletResponse response = (HttpServletResponse) messageInfo.getResponseMessage();

        Optional<IdpUserInfo> cachedUser = sessionManager.readUser(request);
        if (cachedUser.isPresent()) {
            IdpPrincipal principal = new IdpPrincipal(cachedUser.get());
            registerPrincipal(principal, clientSubject);
            LoggerUtils.logSuccess(LoggerUtils.logger(getClass()), principal.getName());
            return AuthStatus.SUCCESS;
        }

        String samlResponse = request.getParameter("SAMLResponse");
        String idToken = request.getParameter("id_token");

        if (samlResponse != null || idToken != null) {
            try {
                IdpUserInfo userInfo = responseValidator.validateResponse(samlResponse, idToken, request);
                IdpPrincipal principal = new IdpPrincipal(userInfo);
                sessionManager.storeUser(request, userInfo);
                registerPrincipal(principal, clientSubject);
                LoggerUtils.logSuccess(LoggerUtils.logger(getClass()), principal.getName());
                return AuthStatus.SUCCESS;
            } catch (IdpValidationException ex) {
                throw new AuthException("Validation failed: " + ex.getMessage());
            }
        }

        String redirect = authRequestBuilder.buildAuthRequestUrl(request);
        try {
            LoggerUtils.logRedirect(LoggerUtils.logger(getClass()), redirect);
            response.sendRedirect(redirect);
            return AuthStatus.SEND_CONTINUE;
        } catch (IOException e) {
            throw new AuthException("Unable to redirect to IdP: " + e.getMessage());
        }
    }

    @Override
    public AuthStatus secureResponse(MessageInfo messageInfo, Subject serviceSubject) throws AuthException {
        return AuthStatus.SEND_SUCCESS;
    }

    @Override
    public void cleanSubject(MessageInfo messageInfo, Subject subject) throws AuthException {
        HttpServletRequest request = (HttpServletRequest) messageInfo.getRequestMessage();
        sessionManager.clear(request);
        if (subject != null) {
            subject.getPrincipals().clear();
            subject.getPublicCredentials().clear();
            subject.getPrivateCredentials().clear();
        }
    }

    private void registerPrincipal(IdpPrincipal principal, Subject subject) throws AuthException {
        try {
            callbackHandler.handle(new Callback[]{
                    new CallerPrincipalCallback(subject, principal),
                    new GroupPrincipalCallback(subject, principal.getRoles().toArray(new String[0]))
            });
        } catch (Exception e) {
            throw new AuthException("Failed to propagate principal: " + e.getMessage());
        }
    }
}
