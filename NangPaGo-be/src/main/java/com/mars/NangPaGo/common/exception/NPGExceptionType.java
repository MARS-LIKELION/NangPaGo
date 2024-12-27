package com.mars.NangPaGo.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum NPGExceptionType {
    // Success
    OK(HttpStatus.OK, "Success"),

    // BAD_REQUEST(400)
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청"),

    // (404)
    NOT_FOUND(HttpStatus.NOT_FOUND, "데이터가 존재하지 않음"),

    // Internal Server Error(500)
    SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 에러"),
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
