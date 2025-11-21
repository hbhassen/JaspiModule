package com.example.idp;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ConfigPropertiesTest {

    @Test
    void shouldExposeValuesAndDefaults() {
        ConfigProperties properties = new ConfigProperties(Map.of(
            ConfigProperties.IDP_AUTH_ENDPOINT, "https://idp/auth",
            ConfigProperties.CLIENT_ID, "client",
            ConfigProperties.REDIRECT_URI, "https://app/callback"
        ));

        assertEquals("https://idp/auth", properties.getAuthEndpoint());
        assertEquals("client", properties.getClientId());
        assertEquals("https://app/callback", properties.getRedirectUri());
        assertEquals("roles", properties.getRoleClaim());
        assertTrue(properties.asMap().containsKey(ConfigProperties.CLIENT_ID));
    }

    @Test
    void shouldValidateMandatoryFields() {
        ConfigProperties properties = new ConfigProperties(Map.of());
        IdpConfigurationException exception = assertThrows(IdpConfigurationException.class, properties::validate);
        assertTrue(exception.getMessage().contains("Authentication endpoint"));
    }
}
