package com.voleksiienko.specforgeapi.infra.adapter.in.web.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record BaseConfigDto(
        @NotNull NamingDto naming, @NotNull FieldsDto fields) {

    public record NamingDto(@NotBlank String className) {}

    public record FieldsDto(@NotNull SortTypeDto sort) {
        public enum SortTypeDto {
            AS_IS,
            ALPHABETICAL,
            REQUIRED_FIRST
        }
    }
}
