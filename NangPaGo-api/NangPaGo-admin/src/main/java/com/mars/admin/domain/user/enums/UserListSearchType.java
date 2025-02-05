package com.mars.admin.domain.user.enums;

import lombok.Getter;

@Getter
public enum UserListSearchType {
    EMAIL("email"),
    NICKNAME("nickname");

    private final String key;

    UserListSearchType(String key) {
        this.key = key;
    }
}
