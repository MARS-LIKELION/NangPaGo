package com.mars.NangPaGo.domain.auth.factory;

import com.mars.NangPaGo.auth.enums.OAuth2Provider;
import com.mars.NangPaGo.domain.auth.factory.oauth2tokeninfo.GoogleTokenInfo;
import com.mars.NangPaGo.domain.auth.factory.oauth2tokeninfo.KakaoTokenInfo;
import com.mars.NangPaGo.domain.auth.factory.oauth2tokeninfo.NaverTokenInfo;
import com.mars.NangPaGo.domain.auth.factory.oauth2tokeninfo.OAuth2TokenInfo;
import java.util.Map;
import org.springframework.stereotype.Component;


@Component
public class OAuth2TokenFactory {

    private final Map<OAuth2Provider, OAuth2TokenInfo> TOKEN_INFO_CREATORS;

    public OAuth2TokenFactory(GoogleTokenInfo googleTokenInfo, KakaoTokenInfo kakaoTokenInfo,
        NaverTokenInfo naverTokenInfo) {
        this.TOKEN_INFO_CREATORS = Map.of(
            OAuth2Provider.GOOGLE, googleTokenInfo,
            OAuth2Provider.KAKAO, kakaoTokenInfo,
            OAuth2Provider.NAVER, naverTokenInfo
        );
    }

    public OAuth2TokenInfo create(String providerName) {
        return TOKEN_INFO_CREATORS.get(OAuth2Provider.from(providerName));
    }

}
