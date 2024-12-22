package com.mars.NangPaGo.domain.user.factory;

import static com.mars.NangPaGo.domain.user.enums.Provider.*;

import com.mars.NangPaGo.domain.user.enums.Provider;
import com.mars.NangPaGo.domain.user.factory.userinfo.GoogleUserInfo;
import com.mars.NangPaGo.domain.user.factory.userinfo.KakaoUserInfo;
import com.mars.NangPaGo.domain.user.factory.userinfo.NaverUserInfo;
import com.mars.NangPaGo.domain.user.factory.userinfo.OAuth2UserInfo;

import java.util.Map;
import java.util.function.Function;

public class OAuth2UserInfoFactory {

    private static final Map<Provider, Function<Map<String, Object>, OAuth2UserInfo>> USER_INFO_CREATORS =
            Map.of(
                    GOOGLE, GoogleUserInfo::new,
                    KAKAO, KakaoUserInfo::new,
                    NAVER, NaverUserInfo::new
            );

    public static OAuth2UserInfo create(String providerName, Map<String, Object> attributes) {
        Provider provider = Provider.from(providerName)
                .orElseThrow(() -> new IllegalArgumentException("Invalid provider: " + providerName));

        Function<Map<String, Object>, OAuth2UserInfo> creator = USER_INFO_CREATORS.get(provider);
        validateProvider(creator, provider);
        return creator.apply(attributes);
    }

    private static void validateProvider(Function<Map<String, Object>, OAuth2UserInfo> creator, Provider provider) {
        if (creator == null) {
            throw new IllegalArgumentException("유효하지 않는 제공자: " + provider);
        }
    }
}

