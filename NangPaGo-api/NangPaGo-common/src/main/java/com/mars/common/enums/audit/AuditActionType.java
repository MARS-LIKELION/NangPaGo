package com.mars.common.enums.audit;

public enum AuditActionType {

    // Recipe Comment
    RECIPE_COMMENT_CREATE,
    RECIPE_COMMENT_UPDATE,
    RECIPE_COMMENT_DELETE,

    // Community
    COMMUNITY_CREATE,
    COMMUNITY_UPDATE,
    COMMUNITY_DELETE,

    // Community Comment
    COMMUNITY_COMMENT_CREATE,
    COMMUNITY_COMMENT_UPDATE,
    COMMUNITY_COMMENT_DELETE,

    // User Info
    USER_INFO_UPDATE,
    USER_DEACTIVATE,
    USER_LOGIN,
    USER_SIGNUP, // TODO: 사용자 회원가입 구분 (현재는 사용자 로그인과 사용자 회원가입의 구분이 없음)

    ;
}
