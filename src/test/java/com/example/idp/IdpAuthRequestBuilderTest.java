package com.example.idp;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

class IdpAuthRequestBuilderTest {

    @Test
    void buildAuthRequestUrlShouldIncludeParameters() {
        ConfigProperties config = ConfigProperties.builder()
                .idpAuthorizeEndpoint("https://idp/auth")
                .clientId("client")
                .redirectUri("https://app/callback")
                .requestedScopes("openid email")
                .build();
        IdpAuthRequestBuilder builder = new IdpAuthRequestBuilder(config);

        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpSession session = Mockito.mock(HttpSession.class);
        when(request.getSession(true)).thenReturn(session);
        when(session.getId()).thenReturn("state123");

        String url = builder.buildAuthRequestUrl(request);

        assertTrue(url.contains("client_id=client"));
        assertTrue(url.contains("redirect_uri=https%3A%2F%2Fapp%2Fcallback"));
        assertTrue(url.contains("scope=openid+email"));
        assertTrue(url.contains("state=state123"));
    }
}
