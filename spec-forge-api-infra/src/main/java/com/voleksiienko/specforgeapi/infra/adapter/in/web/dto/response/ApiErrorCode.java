package com.voleksiienko.specforgeapi.infra.adapter.in.web.dto.response;

import com.voleksiienko.specforgeapi.core.domain.model.error.ErrorCode;

public enum ApiErrorCode implements ErrorCode {
    INTERNAL,
    INVALID_REQUEST_FORMAT;

    @Override
    public String getName() {
        return name();
    }
}
