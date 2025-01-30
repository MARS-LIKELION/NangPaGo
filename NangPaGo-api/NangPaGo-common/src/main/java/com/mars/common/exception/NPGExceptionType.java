package com.mars.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum NPGExceptionType {
    // Success(200)
    OK(HttpStatus.OK, "Success"),

    // BAD_REQUEST(400)
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청"),
    BAD_REQUEST_INVALID(HttpStatus.BAD_REQUEST, "유효하지 않은 요청"),
    BAD_REQUEST_INVALID_COMMENT(HttpStatus.BAD_REQUEST, "댓글 내용은 비어 있을 수 없습니다."),
    BAD_REQUEST_INVALID_PAGE_NO(HttpStatus.BAD_REQUEST, "pageNo 는 양수로 입력해야 합니다."),
    BAD_REQUEST_INVALID_PAGE_SIZE(HttpStatus.BAD_REQUEST, "pageSize 는 양수로 입력해야 합니다."),
    BAD_REQUEST_CHECK_NICKNAME(HttpStatus.BAD_REQUEST, "닉네임 중복 검사를 해야합니다."),
    BAD_REQUEST_UNUSABLE_NICKNAME(HttpStatus.BAD_REQUEST, "사용할 수 없는 닉네임입니다."),
    BAD_REQUEST_DISCONNECT_THIRD_PARTY(HttpStatus.BAD_REQUEST, "서드 파티 연결에 실패했습니다."),

    // UNAUTHORIZED(401)
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증 실패"),
    UNAUTHORIZED_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "만료된 토큰"),
    UNAUTHORIZED_TOKEN_MISSING(HttpStatus.UNAUTHORIZED, "JWT 토큰이 없습니다."),
    UNAUTHORIZED_NO_AUTHENTICATION_CONTEXT(HttpStatus.UNAUTHORIZED, "인증 정보가 존재하지 않습니다."),
    UNAUTHORIZED_OAUTH2_PROVIDER_TOKEN(HttpStatus.UNAUTHORIZED, "Oauth2 프로바이더의 토큰을 발급 받을 수 없습니다."),

    // Forbidden(403)
    FORBIDDEN(HttpStatus.FORBIDDEN, "권한이 없습니다."),
    FORBIDDEN_UPDATE_COMMENT(HttpStatus.FORBIDDEN, "댓글을 수정할 권한이 없습니다."),
    FORBIDDEN_DELETE_COMMENT(HttpStatus.FORBIDDEN, "댓글을 삭제할 권한이 없습니다."),

    // NOT_FOUND(404)
    NOT_FOUND(HttpStatus.NOT_FOUND, "데이터가 존재하지 않음"),
    NOT_FOUND_USER(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    NOT_FOUND_RECIPE(HttpStatus.NOT_FOUND, "레시피를 찾을 수 없습니다."),
    NOT_FOUND_RECIPE_FAVORITE(HttpStatus.NOT_FOUND, "즐겨찾기한 레시피가 없습니다."),
    NOT_FOUND_COMMENT(HttpStatus.NOT_FOUND, "댓글을 찾을 수 없습니다."),
    NOT_FOUND_INGREDIENT(HttpStatus.NOT_FOUND, "등록된 식재료 정보를 찾을 수 없습니다."),
    NOT_FOUND_COMMUNITY(HttpStatus.NOT_FOUND, "게시물을 찾을 수 없습니다."),
    NOT_FOUND_COMMUNITY_COMMENT(HttpStatus.NOT_FOUND, "게시물 내 댓글을 찾을 수 없습니다."),
    NOT_FOUND_OAUTH2_PROVIDER_TOKEN(HttpStatus.NOT_FOUND, "Oauth2 프로바이더의 토큰을 찾을 수 없습니다."),

    // Conflict(409)
    DUPLICATE_INGREDIENT(HttpStatus.CONFLICT, "이미 등록되어있는 정보입니다."),
    
    // Unprocessable Entity(422)
    UNPROCESSABLE_JSON(HttpStatus.UNPROCESSABLE_ENTITY, "JSON 객체 변환에 실패했습니다."),

    // Internal Server Error(500)
    SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 에러"),
    SERVER_ERROR_ELASTICSEARCH(HttpStatus.INTERNAL_SERVER_ERROR, "Elasticsearch 서버 에러"),
    SERVER_ERROR_IMAGE_UPLOAD(HttpStatus.INTERNAL_SERVER_ERROR, "이미지 업로드 중 서버 오류"),
    SERVER_ERROR_RABBITMQ_CONNECTION(HttpStatus.INTERNAL_SERVER_ERROR, "RabbitMQ 연결에 실패했습니다."),
    SERVER_ERROR_SEND_LIKE_COUNT(HttpStatus.INTERNAL_SERVER_ERROR, "좋아요 수 전송 중 오류가 발생했습니다."),
    ;

    private final HttpStatus httpStatus;
    private final String defaultMessage;

    public NPGException of() {
        return new NPGException(this);
    }

    public NPGException of(String message) {
        return new NPGException(this, message);
    }
}
