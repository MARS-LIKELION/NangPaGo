package com.mars.NangPaGo.domain.auth.factory.oauth2tokeninfo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class KakaoTokenInfo implements OAuth2TokenInfo {

    @Value("${spring.security.oauth2.client.provider.kakao.token-uri}")
    private String kakaoTokenUri;
    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String kakaoClientId;
    @Value("${spring.security.oauth2.client.provider.kakao.disconnect-uri}")
    private String kakaoDisConnectUri;

    @Override
    public String getTokenUri() {
        return kakaoTokenUri;
    }

    @Override
    public String getDisconnectUri(String accessToken) {
        return kakaoDisConnectUri;
    }

    @Override
    public String getRequestBody(String refreshToken) {
        return "grant_type=refresh_token&refresh_token=" + refreshToken + "&client_id=" + kakaoClientId;
    }
}
