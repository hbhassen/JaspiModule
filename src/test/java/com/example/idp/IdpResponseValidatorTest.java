package com.example.idp;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class IdpResponseValidatorTest {

    @Test
    void shouldValidateSamlResponse() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getParameter("SAMLResponse")).thenReturn("token");
        when(request.getRequestURL()).thenReturn(new StringBuffer("http://localhost/cb"));

        TokenValidator validator = mock(TokenValidator.class);
        when(validator.validate("token", "http://localhost/cb")).thenReturn(new IdpUserInfo("bob", Set.of("user")));

        IdpResponseValidator responseValidator = new IdpResponseValidator(validator);
        IdpUserInfo info = responseValidator.validateResponse(request);
        assertEquals("bob", info.getSubject());
    }

    @Test
    void shouldRejectMissingToken() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        TokenValidator validator = mock(TokenValidator.class);
        IdpResponseValidator responseValidator = new IdpResponseValidator(validator);
        assertThrows(IdpValidationException.class, () -> responseValidator.validateResponse(request));
    }
}
