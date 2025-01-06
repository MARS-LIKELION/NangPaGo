package com.mars.NangPaGo.auth.enums;

import java.util.Arrays;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum OAuth2Provider {
    GOOGLE("google"),
    KAKAO("kakao"),
    NAVER("naver");

    private final String name;

    public static OAuth2Provider from(String name) {
        return Arrays.stream(OAuth2Provider.values())
                .filter(oauth2Provider -> oauth2Provider.name.equalsIgnoreCase(name))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 제공자: " + name));
    }
}
