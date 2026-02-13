package com.voleksiienko.specforgeapi.core.domain.model.config;

import com.voleksiienko.specforgeapi.core.domain.exception.ConfigValidationException;
import java.util.Objects;

public record JavaConfig(
        BaseConfig base, Structure structure, Validation validation, Builder builder, Serialization serialization)
        implements GenerationConfig {

    public JavaConfig {
        if (Objects.isNull(base)) {
            throw new ConfigValidationException("Base configuration is mandatory");
        }
        if (Objects.isNull(structure)) {
            throw new ConfigValidationException("Structure configuration is mandatory");
        }
        if (Objects.isNull(validation)) {
            throw new ConfigValidationException("Validation configuration is mandatory");
        }
        if (Objects.isNull(builder)) {
            throw new ConfigValidationException("Builder configuration is mandatory");
        }
        if (Objects.isNull(serialization)) {
            throw new ConfigValidationException("Serialization configuration is mandatory");
        }
    }

    public record Structure(Type type) {
        public Structure {
            if (Objects.isNull(type)) {
                throw new ConfigValidationException("Structure type is mandatory");
            }
        }

        public enum Type {
            CLASS,
            RECORD
        }
    }

    public record Validation(boolean enabled) {}

    public record Builder(boolean enabled, boolean onlyIfMultipleFields) {}

    public record Serialization(JsonPropertyMode jsonPropertyMode) {
        public Serialization {
            if (Objects.isNull(jsonPropertyMode)) {
                throw new ConfigValidationException("JSON Property mode is mandatory");
            }
        }

        public enum JsonPropertyMode {
            ALWAYS,
            NEVER
        }
    }
}
