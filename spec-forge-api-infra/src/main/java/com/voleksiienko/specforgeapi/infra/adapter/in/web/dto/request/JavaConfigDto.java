package com.voleksiienko.specforgeapi.infra.adapter.in.web.dto.request;

import jakarta.validation.constraints.NotNull;

public record JavaConfigDto(
        @NotNull BaseConfigDto base,
        @NotNull StructureDto structure,
        @NotNull ValidationDto validation,
        @NotNull BuilderDto builder,
        @NotNull SerializationDto serialization)
        implements GenerationConfigDto {

    public record StructureDto(@NotNull TypeDto type) {
        public enum TypeDto {
            CLASS,
            RECORD
        }
    }

    public record ValidationDto(@NotNull Boolean enabled) {}

    public record BuilderDto(
            @NotNull Boolean enabled, @NotNull Boolean onlyIfMultipleFields) {}

    public record SerializationDto(@NotNull JsonPropertyModeDto jsonPropertyMode) {
        public enum JsonPropertyModeDto {
            ALWAYS,
            NEVER
        }
    }
}
