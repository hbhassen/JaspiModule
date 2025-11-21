package com.example.idp;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SessionManagerTest {

    @Test
    void shouldStoreAndRetrievePrincipal() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);
        when(request.getSession(true)).thenReturn(session);
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute(SessionManager.PRINCIPAL_SESSION_KEY)).thenReturn(new IdpPrincipal("user", Set.of("role")));

        SessionManager manager = new SessionManager();
        IdpPrincipal principal = new IdpPrincipal("user", Set.of("role"));
        manager.storePrincipal(request, principal);
        verify(session).setAttribute(SessionManager.PRINCIPAL_SESSION_KEY, principal);

        IdpPrincipal result = manager.getPrincipal(request);
        assertNotNull(result);
        assertEquals("user", result.getName());
    }

    @Test
    void shouldReturnNullWhenNoSession() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getSession(false)).thenReturn(null);
        SessionManager manager = new SessionManager();
        assertNull(manager.getPrincipal(request));
    }

    @Test
    void shouldClearPrincipal() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);
        when(request.getSession(false)).thenReturn(session);
        SessionManager manager = new SessionManager();
        manager.clearPrincipal(request);
        verify(session).removeAttribute(SessionManager.PRINCIPAL_SESSION_KEY);
    }
}
