package com.example.idp;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class IdpPrincipalTest {

    @Test
    void shouldExposeNameAndRoles() {
        IdpPrincipal principal = new IdpPrincipal("alice", Set.of("admin"));
        assertEquals("alice", principal.getName());
        assertTrue(principal.getRoles().contains("admin"));
    }

    @Test
    void shouldValidateName() {
        assertThrows(IllegalArgumentException.class, () -> new IdpPrincipal(" ", Set.of("user")));
    }

    @Test
    void shouldImplementEquality() {
        IdpPrincipal one = new IdpPrincipal("bob", Set.of("user"));
        IdpPrincipal two = new IdpPrincipal("bob", Set.of("user"));
        assertEquals(one, two);
        assertEquals(one.hashCode(), two.hashCode());
    }
}
