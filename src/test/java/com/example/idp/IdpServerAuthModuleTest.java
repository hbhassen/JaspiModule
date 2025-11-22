package com.example.idp;

import jakarta.security.auth.message.AuthException;
import jakarta.security.auth.message.AuthStatus;
import jakarta.security.auth.message.MessageInfo;
import jakarta.security.auth.message.MessagePolicy;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class IdpServerAuthModuleTest {

    private IdpServerAuthModule module;
    private CallbackHandler callbackHandler;
    private Map<String, Object> options;

    @BeforeEach
    void setUp() throws Exception {
        module = new IdpServerAuthModule();
        callbackHandler = mock(CallbackHandler.class);
        options = new HashMap<>();
        options.put("idp.authorization.endpoint", "https://idp/auth");
        options.put("idp.client.id", "client");
        options.put("app.redirect.uri", "https://app/cb");
        module.initialize(mock(MessagePolicy.class), mock(MessagePolicy.class), callbackHandler, options);
    }

    @Test
    void shouldSendRedirectWhenNoSessionOrResponse() throws Exception {
        MessageInfo info = mock(MessageInfo.class);
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(info.getRequestMessage()).thenReturn(request);
        when(info.getResponseMessage()).thenReturn(response);
        when(info.getMap()).thenReturn(new HashMap<>());

        AuthStatus status = module.validateRequest(info, new Subject(), new Subject());
        assertEquals(AuthStatus.SEND_CONTINUE, status);
        verify(response).sendRedirect(startsWith("https://idp/auth"));
    }

    @Test
    void shouldValidateExistingSession() throws Exception {
        MessageInfo info = mock(MessageInfo.class);
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);
        when(info.getRequestMessage()).thenReturn(request);
        when(info.getResponseMessage()).thenReturn(response);
        when(info.getMap()).thenReturn(new HashMap<>());

        IdpUserInfo infoUser = new IdpUserInfo("alice", Set.of("user"));
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute(SessionManager.SESSION_ATTRIBUTE)).thenReturn(infoUser);

        AuthStatus status = module.validateRequest(info, new Subject(), new Subject());
        assertEquals(AuthStatus.SUCCESS, status);
    }

    @Test
    void shouldValidateIdpResponse() throws Exception {
        MessageInfo info = mock(MessageInfo.class);
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);
        when(info.getRequestMessage()).thenReturn(request);
        when(info.getResponseMessage()).thenReturn(response);
        when(info.getMap()).thenReturn(new HashMap<>());
        when(request.getParameter("id_token")).thenReturn("token");
        when(request.getRequestURL()).thenReturn(new StringBuffer("https://app/cb"));
        when(request.getSession(true)).thenReturn(session);

        AuthStatus status = module.validateRequest(info, new Subject(), new Subject());
        assertEquals(AuthStatus.SUCCESS, status);
        verify(callbackHandler, atLeastOnce()).handle(any(Callback[].class));
    }

    @Test
    void shouldHandleCallbackFailure() throws Exception {
        MessageInfo info = mock(MessageInfo.class);
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);
        when(info.getRequestMessage()).thenReturn(request);
        when(info.getResponseMessage()).thenReturn(response);
        when(info.getMap()).thenReturn(new HashMap<>());
        when(request.getParameter("id_token")).thenReturn("token");
        when(request.getRequestURL()).thenReturn(new StringBuffer("https://app/cb"));
        when(request.getSession(true)).thenReturn(session);

        doThrow(new UnsupportedCallbackException(null)).when(callbackHandler).handle(any());
        assertThrows(AuthException.class, () -> module.validateRequest(info, new Subject(), new Subject()));
    }
}
