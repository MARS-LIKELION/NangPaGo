package com.mars.app.domain.user.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Getter
@AllArgsConstructor
public enum Gender {
    M("male", "M"),
    F("female", "F"),
    NONE("", "");

    private final String kakaoCode;
    private final String naverCode;

    private static final Map<String, Function<String, Gender>> PROVIDER_MAP = new HashMap<>();

    static {
        PROVIDER_MAP.put("kakao", code -> fromSocialCode(code, Gender::getKakaoCode));
        PROVIDER_MAP.put("naver", code -> fromSocialCode(code, Gender::getNaverCode));
        PROVIDER_MAP.put("google", code -> NONE);
    }

    public static Gender fromProvider(String provider, String code) {
        if (code == null || code.trim().isEmpty()) {
            return NONE;
        }
        return PROVIDER_MAP.getOrDefault(provider.toLowerCase(), c -> NONE).apply(code);
    }

    private static Gender fromSocialCode(String code, Function<Gender, String> codeMapper) {
        return Arrays.stream(values())
            .filter(gender -> code.equalsIgnoreCase(codeMapper.apply(gender)))
            .findFirst()
            .orElse(NONE);
    }
}
