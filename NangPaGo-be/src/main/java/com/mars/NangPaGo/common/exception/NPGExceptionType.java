package com.mars.NangPaGo.common.exception;

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

    // UNAUTHORIZED(401)
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증 실패"),
    UNAUTHORIZED_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "만료된 토큰"),

    // NOT_FOUND(404)
    NOT_FOUND(HttpStatus.NOT_FOUND, "데이터가 존재하지 않음"),
    NOT_FOUND_USER(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    NOT_FOUND_RECIPE(HttpStatus.NOT_FOUND, "레시피를 찾을 수 없습니다."),
    NOT_FOUND_COMMENT(HttpStatus.NOT_FOUND, "댓글을 찾을 수 없습니다."),

    // Internal Server Error(500)
    SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 에러"),
    SERVER_ERROR_ELASTICSEARCH(HttpStatus.INTERNAL_SERVER_ERROR, "Elasticsearch 서버 에러"),
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
