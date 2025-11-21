package com.example.idp;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class SessionManagerTest {

    private final SessionManager sessionManager = new SessionManager();

    @Test
    void storeAndReadUserRoundTrip() {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpSession session = Mockito.mock(HttpSession.class);
        when(request.getSession(true)).thenReturn(session);
        when(request.getSession(false)).thenReturn(session);
        ConcurrentMap<String, Object> state = new ConcurrentHashMap<>();
        Mockito.doAnswer(invocation -> {
            state.put(invocation.getArgument(0), invocation.getArgument(1));
            return null;
        }).when(session).setAttribute(Mockito.anyString(), Mockito.any());
        Mockito.doAnswer(invocation -> state.get(invocation.getArgument(0)))
                .when(session).getAttribute(Mockito.anyString());

        IdpUserInfo userInfo = IdpUserInfo.builder().username("alice").addRole("admin").build();
        sessionManager.storeUser(request, userInfo);

        Optional<IdpUserInfo> result = sessionManager.readUser(request);
        assertTrue(result.isPresent());
        assertEquals("alice", result.get().getUsername());
    }

    @Test
    void clearRemovesStoredUser() {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpSession session = Mockito.mock(HttpSession.class);
        when(request.getSession(false)).thenReturn(session);

        sessionManager.clear(request);
        Mockito.verify(session).removeAttribute(SessionManager.SESSION_KEY);
    }
}
