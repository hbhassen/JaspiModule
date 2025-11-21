package com.example.idp;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class IdpResponseValidatorTest {

    @Test
    void shouldValidateAndReturnUserInfo() {
        IdpClient client = mock(IdpClient.class);
        TokenValidator validator = mock(TokenValidator.class);
        ConfigProperties properties = new ConfigProperties(Map.of(
            ConfigProperties.IDP_AUTH_ENDPOINT, "https://idp/auth",
            ConfigProperties.CLIENT_ID, "client",
            ConfigProperties.REDIRECT_URI, "https://app/callback"
        ));

        Map<String, String> exchanged = Map.of("sub", "user", "roles", "admin");
        when(client.exchange("token")).thenReturn(exchanged);
        IdpUserInfo expected = IdpUserInfo.builder("user").addRole("admin").build();
        when(validator.validate(exchanged, "roles")).thenReturn(expected);

        IdpResponseValidator responseValidator = new IdpResponseValidator(client, validator, properties);
        IdpUserInfo result = responseValidator.validateResponse(null, "token");

        assertEquals(expected, result);
    }

    @Test
    void shouldRejectMissingPayload() {
        IdpResponseValidator responseValidator = new IdpResponseValidator(payload -> Map.of(), (map, roleClaim) -> IdpUserInfo.builder("x").build(),
            new ConfigProperties(Map.of(
                ConfigProperties.IDP_AUTH_ENDPOINT, "https://idp/auth",
                ConfigProperties.CLIENT_ID, "client",
                ConfigProperties.REDIRECT_URI, "https://app/callback"
            )));

        assertThrows(IdpValidationException.class, () -> responseValidator.validateResponse(null, ""));
    }
}
