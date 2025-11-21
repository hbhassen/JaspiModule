package com.example.idp;

import jakarta.security.auth.message.AuthStatus;
import jakarta.security.auth.message.MessageInfo;
import jakarta.security.auth.message.callback.CallerPrincipalCallback;
import jakarta.security.auth.message.callback.GroupPrincipalCallback;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.Callback;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class IdpServerAuthModuleTest {

    private Map<String, String> options;

    @BeforeEach
    void setup() {
        options = Map.of(
            ConfigProperties.IDP_AUTH_ENDPOINT, "https://idp/auth",
            ConfigProperties.CLIENT_ID, "client",
            ConfigProperties.REDIRECT_URI, "https://app/callback"
        );
    }

    @Test
    void shouldReturnSuccessWhenAlreadyAuthenticated() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);
        IdpPrincipal principal = new IdpPrincipal("user", Set.of("role"));
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute(SessionManager.PRINCIPAL_SESSION_KEY)).thenReturn(principal);

        AtomicReference<IdpPrincipal> propagated = new AtomicReference<>();
        CallbackHandler handler = callbacks -> {
            for (Callback callback : callbacks) {
                if (callback instanceof CallerPrincipalCallback caller) {
                    propagated.set((IdpPrincipal) caller.getPrincipal());
                }
            }
        };

        IdpServerAuthModule module = new IdpServerAuthModule();
        module.initialize(null, null, handler, options);
        AuthStatus status = module.validateRequest(new StubMessageInfo(request, response), new Subject(), new Subject());

        assertEquals(AuthStatus.SUCCESS, status);
        assertEquals(principal, propagated.get());
    }

    @Test
    void shouldRedirectWhenNoAuthenticationPresent() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(request.getSession(false)).thenReturn(null);

        IdpServerAuthModule module = new IdpServerAuthModule();
        module.initialize(null, null, callbacks -> {}, options);
        StubMessageInfo messageInfo = new StubMessageInfo(request, response);

        AuthStatus status = module.validateRequest(messageInfo, new Subject(), new Subject());

        assertEquals(AuthStatus.SEND_CONTINUE, status);
        verify(response).sendRedirect(anyString());
        assertEquals(Boolean.TRUE.toString(), messageInfo.getMap().get("javax.servlet.http.registerSession"));
    }

    @Test
    void shouldValidateIncomingToken() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);
        when(request.getSession(false)).thenReturn(null);
        when(request.getSession(true)).thenReturn(session);
        when(request.getParameter("id_token")).thenReturn("token-value");

        AtomicReference<String[]> rolesCaptured = new AtomicReference<>();
        AtomicReference<IdpPrincipal> principalCaptured = new AtomicReference<>();
        CallbackHandler handler = callbacks -> {
            for (Callback callback : callbacks) {
                if (callback instanceof CallerPrincipalCallback caller) {
                    principalCaptured.set((IdpPrincipal) caller.getPrincipal());
                }
                if (callback instanceof GroupPrincipalCallback groupCallback) {
                    rolesCaptured.set(groupCallback.getGroups());
                }
            }
        };

        IdpServerAuthModule module = new IdpServerAuthModule();
        module.initialize(null, null, handler, options);
        AuthStatus status = module.validateRequest(new StubMessageInfo(request, response), new Subject(), new Subject());

        assertEquals(AuthStatus.SUCCESS, status);
        assertNotNull(principalCaptured.get());
        assertEquals("token-value", principalCaptured.get().getName());
        assertTrue(rolesCaptured.get().length > 0);
        verify(session).setAttribute(eq(SessionManager.PRINCIPAL_SESSION_KEY), any());
    }

    private static class StubMessageInfo implements MessageInfo {
        private Object request;
        private Object response;
        private final Map<String, Object> map = new HashMap<>();

        StubMessageInfo(Object request, Object response) {
            this.request = request;
            this.response = response;
        }

        @Override
        public Object getRequestMessage() {
            return request;
        }

        @Override
        public Object getResponseMessage() {
            return response;
        }

        @Override
        public void setRequestMessage(Object request) {
            this.request = request;
        }

        @Override
        public void setResponseMessage(Object response) {
            this.response = response;
        }

        @Override
        public Map getMap() {
            return map;
        }
    }
}
