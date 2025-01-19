package com.mars.common.enums.user;

import java.util.Arrays;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum UserStatus {
    ACTIVE("정상"),
    WITHDRAWN("탈퇴"),
    BANNED("밴"),
    OTHERS("기타");

    private final String name;

    public static UserStatus from(String name) {
        return Arrays.stream(UserStatus.values())
            .filter(userStatus -> userStatus.name.equalsIgnoreCase(name))
            .findFirst()
            .orElse(OTHERS);
    }
}
