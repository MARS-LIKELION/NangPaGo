package com.mars.NangPaGo.domain.user.factory;

import static com.mars.NangPaGo.domain.user.enums.Provider.*;

import com.mars.NangPaGo.domain.user.enums.Provider;
import com.mars.NangPaGo.domain.user.factory.userinfo.GoogleUserInfo;
import com.mars.NangPaGo.domain.user.factory.userinfo.KakaoUserInfo;
import com.mars.NangPaGo.domain.user.factory.userinfo.NaverUserInfo;
import com.mars.NangPaGo.domain.user.factory.userinfo.OAuth2UserInfo;

import java.util.Map;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OAuth2UserInfoFactory {

    private static final Map<Provider, Function<Map<String, Object>, OAuth2UserInfo>> USER_INFO_CREATORS =
        Map.of(
            GOOGLE, GoogleUserInfo::new,
            KAKAO, KakaoUserInfo::new,
            NAVER, NaverUserInfo::new
    );

    public static OAuth2UserInfo create(String providerName, Map<String, Object> attributes) {
        return findUserInfo(providerName).apply(attributes);
    }

    public static Function<Map<String, Object>, OAuth2UserInfo> findUserInfo(String providerName) {
        return USER_INFO_CREATORS.get(Provider.from(providerName));
    }
}