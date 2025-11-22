package com.example.idp;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SessionManagerTest {

    @Test
    void shouldStoreAndRetrieveUserInfo() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);
        when(request.getSession(true)).thenReturn(session);
        when(request.getSession(false)).thenReturn(session);
        IdpUserInfo info = new IdpUserInfo("alice", Set.of("user"));

        SessionManager manager = new SessionManager();
        manager.store(request, info);

        when(session.getAttribute(SessionManager.SESSION_ATTRIBUTE)).thenReturn(info);
        Optional<IdpUserInfo> retrieved = manager.retrieve(request);
        assertTrue(retrieved.isPresent());
        assertEquals(info, retrieved.get());
    }

    @Test
    void shouldHandleMissingSession() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getSession(false)).thenReturn(null);
        SessionManager manager = new SessionManager();
        assertTrue(manager.retrieve(request).isEmpty());
    }

    @Test
    void shouldInvalidateSession() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);
        when(request.getSession(false)).thenReturn(session);

        SessionManager manager = new SessionManager();
        manager.invalidate(request);

        verify(session, times(1)).invalidate();
    }
}
