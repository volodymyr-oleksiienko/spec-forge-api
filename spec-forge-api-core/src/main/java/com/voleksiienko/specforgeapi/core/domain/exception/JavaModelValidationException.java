package com.voleksiienko.specforgeapi.core.domain.exception;

public class JavaModelValidationException extends RuntimeException {

    public JavaModelValidationException(String message) {
        super(message);
    }

    public JavaModelValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
