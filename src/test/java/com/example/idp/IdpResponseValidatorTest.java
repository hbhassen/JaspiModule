package com.example.idp;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class IdpResponseValidatorTest {

    @Test
    void validateUsesProvidedToken() throws Exception {
        TokenValidator tokenValidator = token -> IdpUserInfo.builder().username(token + "-parsed").build();
        IdpResponseValidator validator = new IdpResponseValidator(tokenValidator);

        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");

        IdpUserInfo info = validator.validateResponse("samlValue", null, request);
        assertEquals("samlValue-parsed", info.getUsername());
    }

    @Test
    void missingTokensThrows() {
        TokenValidator tokenValidator = token -> IdpUserInfo.builder().username(token).build();
        IdpResponseValidator validator = new IdpResponseValidator(tokenValidator);
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);

        assertThrows(IdpValidationException.class, () -> validator.validateResponse(null, null, request));
    }
}
