package com.voleksiienko.specforgeapi.core.domain.exception;

public class TsModelValidationException extends RuntimeException {

    public TsModelValidationException(String message) {
        super(message);
    }

    public TsModelValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
