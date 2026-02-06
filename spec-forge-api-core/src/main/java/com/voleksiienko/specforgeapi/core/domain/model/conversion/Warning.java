package com.voleksiienko.specforgeapi.core.domain.model.conversion;

import com.voleksiienko.specforgeapi.core.domain.model.error.ErrorCode;

public record Warning(String devMessage, ErrorCode errorCode) {}
