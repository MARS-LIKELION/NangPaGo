package com.mars.NangPaGo.domain.auth.factory.oauth2tokeninfo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class NaverTokenInfo implements OAuth2TokenInfo {

    @Value("${spring.security.oauth2.client.provider.naver.token-uri}")
    private String naverTokenUri;
    @Value("${spring.security.oauth2.client.registration.naver.client-id}")
    private String naverClientId;
    @Value("${spring.security.oauth2.client.registration.naver.client-secret}")
    private String naverClientSecret;
    @Value("${spring.security.oauth2.client.provider.naver.disconnect-uri}")
    private String naverDisConnectUri;

    @Override
    public String getTokenUri() {
        return naverTokenUri;
    }

    @Override
    public String getDisconnectUri(String accessToken) {
        return naverDisConnectUri + accessToken;
    }

    @Override
    public String getRequestBody(String refreshToken) {
        return "grant_type=refresh_token&refresh_token=" + refreshToken + "&client_id=" + naverClientId
            + "&client_secret=" + naverClientSecret;
    }

}
