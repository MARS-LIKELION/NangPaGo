package com.mars.NangPaGo.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum NPGExceptionType {
    // Success
    OK(HttpStatus.OK, "Success"),

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
