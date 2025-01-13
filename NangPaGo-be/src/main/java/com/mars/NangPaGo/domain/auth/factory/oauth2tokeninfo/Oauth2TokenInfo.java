package com.mars.NangPaGo.domain.auth.factory.oauth2tokeninfo;

public interface Oauth2TokenInfo {
    String getTokenUri();

    String getDisconnectUri(String accessToken);
    String getRequestBody(String refreshToken);
}
