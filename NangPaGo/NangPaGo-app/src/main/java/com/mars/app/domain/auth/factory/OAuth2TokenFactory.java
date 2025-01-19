package com.mars.app.domain.auth.factory;

import com.mars.common.enums.oauth.OAuth2Provider;
import com.mars.app.domain.auth.factory.oauth2tokeninfo.GoogleTokenInfo;
import com.mars.app.domain.auth.factory.oauth2tokeninfo.KakaoTokenInfo;
import com.mars.app.domain.auth.factory.oauth2tokeninfo.NaverTokenInfo;
import com.mars.app.domain.auth.factory.oauth2tokeninfo.OAuth2TokenInfo;
import java.util.Map;
import org.springframework.stereotype.Component;


@Component
public class OAuth2TokenFactory {

    private final Map<OAuth2Provider, OAuth2TokenInfo> tokenInfo;

    public OAuth2TokenFactory(GoogleTokenInfo googleTokenInfo, KakaoTokenInfo kakaoTokenInfo,
        NaverTokenInfo naverTokenInfo) {
        this.tokenInfo = Map.of(
            OAuth2Provider.GOOGLE, googleTokenInfo,
            OAuth2Provider.KAKAO, kakaoTokenInfo,
            OAuth2Provider.NAVER, naverTokenInfo
        );
    }

    public OAuth2TokenInfo from(String providerName) {
        return tokenInfo.get(OAuth2Provider.from(providerName));
    }

}
