package com.mars.common.enums.comment;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum UserRecipeCommentStatus {
    ACTIVE("정상"),
    DELETED("삭제됨");

    private final String description;
}
