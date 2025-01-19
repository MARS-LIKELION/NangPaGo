package com.mars.app.auth.factory;

import static com.mars.common.enums.oauth.OAuth2Provider.*;

import com.mars.common.enums.oauth.OAuth2Provider;
import com.mars.common.auth.oauth.GoogleUserInfo;
import com.mars.common.auth.oauth.KakaoUserInfo;
import com.mars.common.auth.oauth.NaverUserInfo;
import com.mars.common.auth.oauth.OAuth2UserInfo;
import java.util.Map;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OAuth2UserInfoFactory {

    private OAuth2UserInfoFactory() { }

    private static final Map<OAuth2Provider, Function<Map<String, Object>, OAuth2UserInfo>> USER_INFO_CREATORS =
        Map.of(
            GOOGLE, GoogleUserInfo::new,
            KAKAO, KakaoUserInfo::new,
            NAVER, NaverUserInfo::new
    );

    public static OAuth2UserInfo create(String providerName, Map<String, Object> attributes) {
        return findUserInfo(providerName).apply(attributes);
    }

    public static Function<Map<String, Object>, OAuth2UserInfo> findUserInfo(String providerName) {
        return USER_INFO_CREATORS.get(OAuth2Provider.from(providerName));
    }
}
