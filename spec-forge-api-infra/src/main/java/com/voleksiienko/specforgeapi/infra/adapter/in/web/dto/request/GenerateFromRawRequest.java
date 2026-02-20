package com.voleksiienko.specforgeapi.infra.adapter.in.web.dto.request;

import jakarta.validation.constraints.NotBlank;

public record GenerateFromRawRequest(@NotBlank String content, GenerationConfigDto generationConfig) {}
