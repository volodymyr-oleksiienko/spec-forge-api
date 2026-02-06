package com.voleksiienko.specforgeapi.core.application.exception;

import com.voleksiienko.specforgeapi.core.domain.model.error.ErrorCode;

public class ConversionException extends RuntimeException {

    private final ErrorCode errorCode;

    public ConversionException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public ConversionException(String message, Throwable cause, ErrorCode errorCode) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
