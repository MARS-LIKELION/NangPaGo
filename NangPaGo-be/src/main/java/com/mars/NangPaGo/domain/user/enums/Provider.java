package com.mars.NangPaGo.domain.user.enums;

import java.util.Arrays;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Provider {
    GOOGLE("google"),
    KAKAO("kakao"),
    NAVER("naver");

    private final String name;

    public static Provider from(String name) {
        return Arrays.stream(Provider.values())
                .filter(provider -> provider.name.equalsIgnoreCase(name))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 제공자: " + name));
    }
}
