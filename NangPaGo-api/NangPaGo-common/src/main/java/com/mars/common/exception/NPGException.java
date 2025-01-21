package com.mars.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class NPGException extends RuntimeException {
    private NPGExceptionType npgExceptionType;
    private String message;

    public NPGException(NPGExceptionType npgExceptionType) {
        this.npgExceptionType = npgExceptionType;
        this.message = npgExceptionType.getDefaultMessage();
    }

    public NPGException(NPGExceptionType npgExceptionType, String message) {
        this.npgExceptionType = npgExceptionType;
        this.message = message;
    }

    public boolean isMessageNotEmpty() {
        return this.message != null && !this.message.isEmpty();
    }

    public boolean isInternalServerError() {
        return this.npgExceptionType.getHttpStatus() == HttpStatus.INTERNAL_SERVER_ERROR;
    }
}
