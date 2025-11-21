package com.example.idp;

import jakarta.security.auth.message.AuthStatus;
import jakarta.security.auth.message.MessageInfo;
import jakarta.security.auth.message.MessagePolicy;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class IdpServerAuthModuleTest {

    private IdpServerAuthModule module;
    private CallbackHandler callbackHandler;
    private MessageInfo messageInfo;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private HttpSession session;

    @BeforeEach
    void setup() throws Exception {
        module = new IdpServerAuthModule();
        callbackHandler = Mockito.mock(CallbackHandler.class);
        messageInfo = Mockito.mock(MessageInfo.class);
        request = Mockito.mock(HttpServletRequest.class);
        response = Mockito.mock(HttpServletResponse.class);
        session = Mockito.mock(HttpSession.class);

        Map<String, Object> options = Map.of(
                "idp.authorize.endpoint", "https://idp/auth",
                "idp.client.id", "client",
                "idp.redirect.uri", "https://app/callback"
        );
        MessagePolicy policy = new MessagePolicy(new MessagePolicy.TargetPolicy[] {}, false);
        module.initialize(policy, policy, callbackHandler, options);
    }

    @Test
    void validateRequestUsesCachedUser() throws Exception {
        IdpUserInfo cached = IdpUserInfo.builder().username("cachedUser").addRole("admin").build();
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute(SessionManager.SESSION_KEY)).thenReturn(cached);
        when(messageInfo.getRequestMessage()).thenReturn(request);
        when(messageInfo.getResponseMessage()).thenReturn(response);

        Subject subject = new Subject();
        AuthStatus status = module.validateRequest(messageInfo, subject, new Subject());

        assertEquals(AuthStatus.SUCCESS, status);
        verify(callbackHandler).handle(any(Callback[].class));
    }

    @Test
    void validateRequestRedirectsWhenNoToken() throws Exception {
        when(request.getParameter("SAMLResponse")).thenReturn(null);
        when(request.getParameter("id_token")).thenReturn(null);
        when(request.getSession(true)).thenReturn(session);
        when(session.getId()).thenReturn("abc");
        when(messageInfo.getRequestMessage()).thenReturn(request);
        when(messageInfo.getResponseMessage()).thenReturn(response);

        AuthStatus status = module.validateRequest(messageInfo, new Subject(), new Subject());

        assertEquals(AuthStatus.SEND_CONTINUE, status);
        verify(response).sendRedirect(any(String.class));
    }

    @Test
    void validateRequestRegistersPrincipalOnIdToken() throws Exception {
        when(request.getParameter("SAMLResponse")).thenReturn(null);
        when(request.getParameter("id_token")).thenReturn("jwt-token");
        when(request.getSession(false)).thenReturn(null);
        when(request.getSession(true)).thenReturn(session);
        when(messageInfo.getRequestMessage()).thenReturn(request);
        when(messageInfo.getResponseMessage()).thenReturn(response);

        ArgumentCaptor<Callback[]> callbacks = ArgumentCaptor.forClass(Callback[].class);

        AuthStatus status = module.validateRequest(messageInfo, new Subject(), new Subject());

        assertEquals(AuthStatus.SUCCESS, status);
        verify(callbackHandler).handle(callbacks.capture());
        Callback[] captured = callbacks.getValue();
        assertEquals(2, captured.length);
    }
}
