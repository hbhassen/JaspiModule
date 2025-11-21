package com.example.idp;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

class IdpAuthRequestBuilderTest {

    @Test
    void shouldBuildRedirectUrl() throws Exception {
        ConfigProperties props = new ConfigProperties(Map.of(
                "idp.authorization.endpoint", "https://idp/auth",
                "idp.client.id", "client",
                "app.redirect.uri", "https://app/cb"
        ));
        IdpClient client = context -> Map.of("client_id", "client", "redirect_uri", "https://app/cb");
        IdpAuthRequestBuilder builder = new IdpAuthRequestBuilder(props, client);

        String url = builder.buildRedirectUrl(mock(HttpServletRequest.class));
        assertTrue(url.startsWith("https://idp/auth?"));
        assertTrue(url.contains("client_id=client"));
    }
}
