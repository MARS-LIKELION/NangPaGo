package com.mars.common.enums.audit;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AuditActionType {

    // Recipe Comment
    RECIPE_COMMENT_CREATE("레시피 댓글 작성"),
    RECIPE_COMMENT_UPDATE("레시피 댓글 수정"),
    RECIPE_COMMENT_DELETE("레시피 댓글 삭제"),

    // Community
    COMMUNITY_CREATE("유저 레시피 게시글 작성"),
    COMMUNITY_UPDATE("유저 레시피 게시글 수정"),
    COMMUNITY_DELETE("유저 레시피 게시글 삭제"),

    // Community Comment
    COMMUNITY_COMMENT_CREATE("유저 레시피 댓글 작성"),
    COMMUNITY_COMMENT_UPDATE("유저 레시피 댓글 수정"),
    COMMUNITY_COMMENT_DELETE("유저 레시피 댓글 삭제"),

    // User Info
    USER_INFO_UPDATE("회원 정보 갱신"),
    USER_DEACTIVATE("회원 탈퇴"),
    USER_LOGIN("로그인"),
    USER_SIGNUP("회원가입"), // TODO: 사용자 회원가입 구분 (현재는 사용자 로그인과 사용자 회원가입의 구분이 없음)

    ;

    private final String description;
}
