package com.mars.NangPaGo.domain.auth.factory.oauth2tokeninfo;

public interface OAuth2TokenInfo {
    String getTokenUri();

    String getDisconnectUri(String accessToken);
    String getRequestBody(String refreshToken);
}
