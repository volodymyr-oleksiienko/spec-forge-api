package com.voleksiienko.specforgeapi.infra.adapter.in.web.dto.request;

import com.voleksiienko.specforgeapi.infra.adapter.in.web.dto.SpecModelDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record GenerateFromSpecModelRequest(
        @NotNull @Valid SpecModelDto specModel, GenerationConfigDto generationConfig) {}
