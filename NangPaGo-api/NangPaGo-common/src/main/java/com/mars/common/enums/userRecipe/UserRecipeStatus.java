package com.mars.common.enums.userRecipe;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserRecipeStatus {
    ACTIVE("정상"),
    DELETED("삭제됨");

    private final String description;
}
