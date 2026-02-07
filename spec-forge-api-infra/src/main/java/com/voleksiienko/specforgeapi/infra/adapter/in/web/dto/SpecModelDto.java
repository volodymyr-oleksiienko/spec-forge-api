package com.voleksiienko.specforgeapi.infra.adapter.in.web.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.List;

public record SpecModelDto(
        @NotNull WrapperType wrapperType, @Valid @NotEmpty List<SpecPropertyDto> properties) {

    public enum WrapperType {
        OBJECT,
        LIST
    }

    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type", visible = true)
    @JsonSubTypes({
        @JsonSubTypes.Type(value = SpecTypeDto.BooleanTypeDto.class, name = "BOOLEAN"),
        @JsonSubTypes.Type(value = SpecTypeDto.IntegerTypeDto.class, name = "INTEGER"),
        @JsonSubTypes.Type(value = SpecTypeDto.DoubleTypeDto.class, name = "DOUBLE"),
        @JsonSubTypes.Type(value = SpecTypeDto.DecimalTypeDto.class, name = "DECIMAL"),
        @JsonSubTypes.Type(value = SpecTypeDto.StringTypeDto.class, name = "STRING"),
        @JsonSubTypes.Type(value = SpecTypeDto.EnumTypeDto.class, name = "ENUM"),
        @JsonSubTypes.Type(value = SpecTypeDto.DateTypeDto.class, name = "DATE"),
        @JsonSubTypes.Type(value = SpecTypeDto.TimeTypeDto.class, name = "TIME"),
        @JsonSubTypes.Type(value = SpecTypeDto.DateTimeTypeDto.class, name = "DATE_TIME"),
        @JsonSubTypes.Type(value = SpecTypeDto.ObjectTypeDto.class, name = "OBJECT"),
        @JsonSubTypes.Type(value = SpecTypeDto.ListTypeDto.class, name = "LIST"),
        @JsonSubTypes.Type(value = SpecTypeDto.MapTypeDto.class, name = "MAP")
    })
    public sealed interface SpecTypeDto {

        record BooleanTypeDto(List<String> examples) implements SpecTypeDto {}

        record IntegerTypeDto(Long minimum, Long maximum, List<String> examples) implements SpecTypeDto {}

        record DoubleTypeDto(Double minimum, Double maximum, List<String> examples) implements SpecTypeDto {}

        record DecimalTypeDto(
                BigDecimal minimum,
                BigDecimal maximum,
                @Positive Integer scale,
                List<String> examples) implements SpecTypeDto {}

        record StringTypeDto(
                @Positive Integer minLength,
                @Positive Integer maxLength,
                String pattern,
                StringTypeFormat format,
                List<String> examples)
                implements SpecTypeDto {

            public enum StringTypeFormat {
                EMAIL,
                UUID,
            }
        }

        record EnumTypeDto(@NotEmpty List<String> values, List<String> examples) implements SpecTypeDto {}

        record DateTypeDto(String format, List<String> examples) implements SpecTypeDto {}

        record TimeTypeDto(String format, List<String> examples) implements SpecTypeDto {}

        record DateTimeTypeDto(String format, List<String> examples) implements SpecTypeDto {}

        record ObjectTypeDto(@Valid List<SpecPropertyDto> children) implements SpecTypeDto {}

        record ListTypeDto(
                @Positive Integer minItems,
                @Positive Integer maxItems,
                @Valid @NotNull SpecTypeDto valueType) implements SpecTypeDto {}

        record MapTypeDto(
                @Valid @NotNull SpecTypeDto keyType,
                @Valid @NotNull SpecTypeDto valueType) implements SpecTypeDto {}
    }

    public record SpecPropertyDto(
            @NotNull @Pattern(regexp = "^\\w+$") String name,
            @Valid @NotNull SpecTypeDto type,
            @NotNull Boolean required,
            String description,
            Boolean deprecated) {}
}
