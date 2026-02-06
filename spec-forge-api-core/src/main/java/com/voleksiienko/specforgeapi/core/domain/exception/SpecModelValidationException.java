package com.voleksiienko.specforgeapi.core.domain.exception;

public class SpecModelValidationException extends RuntimeException {

    public SpecModelValidationException(String message) {
        super(message);
    }

    public SpecModelValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
