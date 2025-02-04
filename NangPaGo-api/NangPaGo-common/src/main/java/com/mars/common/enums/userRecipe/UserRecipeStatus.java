package com.mars.common.enums.userRecipe;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum UserRecipeStatus {
    ACTIVE("정상"),
    DELETED("삭제됨");

    private final String description;
}
