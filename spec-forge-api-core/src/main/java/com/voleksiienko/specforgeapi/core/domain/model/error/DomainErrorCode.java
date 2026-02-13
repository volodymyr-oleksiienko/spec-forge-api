package com.voleksiienko.specforgeapi.core.domain.model.error;

public enum DomainErrorCode implements ErrorCode {
    SPEC_MODEL_VALIDATION_FAILED,
    JAVA_MODEL_VALIDATION_FAILED,
    SPEC_TO_JAVA_CONVERSION_FAILED,
    CONFIG_VALIDATION_FAILED;

    @Override
    public String getName() {
        return name();
    }
}
