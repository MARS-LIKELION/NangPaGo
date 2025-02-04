package com.mars.admin.domain.user.enums;

import lombok.Getter;

@Getter
public enum UserListSearchType {
    EMAIL("email"),
    NICKNAME("nickname");

    private final String type;

    UserListSearchType(String type) {
        this.type = type;
    }
}
