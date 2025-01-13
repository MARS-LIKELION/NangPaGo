package com.mars.NangPaGo.domain.auth.factory.oauth2tokeninfo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class GoogleTokenInfo implements OAuth2TokenInfo {

    @Value("${spring.security.oauth2.client.provider.google.token-uri}")
    private String googleTokenUri;
    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;
    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String googleClientSecret;
    @Value("${spring.security.oauth2.client.provider.google.disconnect-uri}")
    private String googleDisConnectUri;

    @Override
    public String getTokenUri() {
        return googleTokenUri;
    }

    @Override
    public String getDisconnectUri(String accessToken) {
        return googleDisConnectUri + accessToken;
    }

    @Override
    public String getRequestBody(String refreshToken) {
        return "grant_type=refresh_token&refresh_token=" + refreshToken + "&client_id=" + googleClientId
            + "&client_secret=" + googleClientSecret;
    }
}
