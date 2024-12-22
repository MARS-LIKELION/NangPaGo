package com.mars.NangPaGo.domain.user.enums;

import java.util.Arrays;
import java.util.Optional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Provider {
    GOOGLE("google"),
    KAKAO("kakao"),
    NAVER("naver");

    private final String name;

    public static Optional<Provider> from(String name) {
        return Arrays.stream(Provider.values())
                .filter(provider -> provider.name.equals(name))
                .findFirst();
    }
}
