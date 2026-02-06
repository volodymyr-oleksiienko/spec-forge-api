package com.voleksiienko.specforgeapi.core.domain.model.error;

public enum DomainErrorCode implements ErrorCode {
    SPEC_VALIDATION_FAILED;

    @Override
    public String getName() {
        return name();
    }
}
