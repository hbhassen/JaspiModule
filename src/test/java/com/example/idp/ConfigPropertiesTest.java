package com.example.idp;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.util.Map;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

class ConfigPropertiesTest {

    @Test
    void builderShouldValidateRequiredFields() {
        Executable executable = () -> ConfigProperties.builder().build();
        assertThrows(IdpConfigurationException.class, executable);
    }

    @Test
    void fromPropertiesBuildsConfig() {
        Properties properties = new Properties();
        properties.setProperty("idp.authorize.endpoint", "https://idp/auth");
        properties.setProperty("idp.client.id", "client");
        properties.setProperty("idp.redirect.uri", "https://app/callback");

        ConfigProperties config = ConfigProperties.fromProperties(properties);
        assertEquals("https://idp/auth", config.getIdpAuthorizeEndpoint());
        assertEquals("client", config.getClientId());
        assertEquals("https://app/callback", config.getRedirectUri());
        assertEquals("openid profile", config.getRequestedScopes());
    }

    @Test
    void fromOptionsSupportsScopes() {
        Map<String, Object> options = Map.of(
                "idp.authorize.endpoint", "https://idp/auth",
                "idp.client.id", "client",
                "idp.redirect.uri", "https://app/callback",
                "idp.requested.scopes", "openid email"
        );

        ConfigProperties config = ConfigProperties.fromOptions(options);
        assertEquals("openid email", config.getRequestedScopes());
    }
}
