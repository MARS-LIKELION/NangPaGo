package com.mars.NangPaGo.domain.user.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.function.Function;

@Getter
@AllArgsConstructor
public enum Gender {
    MALE("M", "male", "M"),
    FEMALE("F", "female", "F"),
    UNKNOWN("None", null, null);

    private final String databaseValue;
    private final String kakaoCode;
    private final String naverCode;

    public Gender fromKakao(String code) {
        return fromSocialCode(code, Gender::getKakaoCode);
    }

    public Gender fromNaver(String code) {
        return fromSocialCode(code, Gender::getNaverCode);
    }

    public Gender fromGoogle(String code) {
        return UNKNOWN;
    }

    private Gender fromSocialCode(String code, Function<Gender, String> codeMapper) {
        return Arrays.stream(values())
            .filter(gender -> code.equalsIgnoreCase(codeMapper.apply(gender)))
            .findFirst()
            .orElse(UNKNOWN);
    }
}
