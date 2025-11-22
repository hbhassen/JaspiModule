package com.example.idp;

import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ConfigPropertiesTest {

    @Test
    void shouldRequireMandatoryProperties() throws Exception {
        ConfigProperties props = new ConfigProperties(Map.of(
                "idp.authorization.endpoint", "https://idp/auth",
                "app.redirect.uri", "https://app/callback",
                "idp.client.id", "client"
        ));
        assertEquals("https://idp/auth", props.getIdpAuthorizationEndpoint());
        assertEquals("https://app/callback", props.getRedirectUri());
        assertEquals("client", props.getClientId());
    }

    @Test
    void shouldThrowOnMissingProperty() {
        ConfigProperties props = new ConfigProperties(Map.of());
        assertThrows(IdpConfigurationException.class, props::getClientId);
    }

    @Test
    void shouldReturnOptionalSecret() {
        ConfigProperties props = new ConfigProperties(Map.of("idp.client.secret", "secret"));
        Optional<String> secret = props.getClientSecret();
        assertTrue(secret.isPresent());
        assertEquals("secret", secret.get());
    }
}
