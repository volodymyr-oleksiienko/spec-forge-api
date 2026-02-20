package com.voleksiienko.specforgeapi.core.domain.model.error;

public enum DomainErrorCode implements ErrorCode {
    SPEC_MODEL_VALIDATION_FAILED,
    JAVA_MODEL_VALIDATION_FAILED,
    TS_MODEL_VALIDATION_FAILED,
    SPEC_TO_JAVA_CONVERSION_FAILED,
    SPEC_TO_TS_CONVERSION_FAILED,
    CONFIG_VALIDATION_FAILED,
    CODE_GENERATION_FAILED;

    @Override
    public String getName() {
        return name();
    }
}
