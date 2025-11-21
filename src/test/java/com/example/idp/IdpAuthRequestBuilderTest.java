package com.example.idp;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

class IdpAuthRequestBuilderTest {

    @Test
    void shouldBuildRedirectUrl() {
        ConfigProperties properties = new ConfigProperties(Map.of(
            ConfigProperties.IDP_AUTH_ENDPOINT, "https://idp/auth",
            ConfigProperties.CLIENT_ID, "client-id",
            ConfigProperties.REDIRECT_URI, "https://app/callback"
        ));
        IdpAuthRequestBuilder builder = new IdpAuthRequestBuilder(properties);

        String url = builder.buildAuthRequest("state-token");
        assertTrue(url.startsWith("https://idp/auth?"));
        assertTrue(url.contains("client_id="));
        assertTrue(url.contains("redirect_uri="));
        assertTrue(url.contains("state-token"));
    }
}
