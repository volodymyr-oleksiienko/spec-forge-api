package com.voleksiienko.specforgeapi.infra.adapter.in.web.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record TypeScriptConfigDto(
        @Valid @NotNull BaseConfigDto base,
        @Valid @NotNull TypeScriptConfigDto.StructureDto structure,
        @Valid @NotNull TypeScriptConfigDto.EnumsDto enums)
        implements GenerationConfigDto {

    public record StructureDto(@NotNull DeclarationStyle style) {
        public enum DeclarationStyle {
            INTERFACE,
            TYPE_ALIAS
        }
    }

    public record EnumsDto(@NotNull EnumStyle style) {
        public enum EnumStyle {
            TS_ENUM,
            UNION_STRING
        }
    }
}
