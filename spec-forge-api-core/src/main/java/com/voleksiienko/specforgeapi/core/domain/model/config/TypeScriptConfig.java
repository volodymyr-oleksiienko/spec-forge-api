package com.voleksiienko.specforgeapi.core.domain.model.config;

import com.voleksiienko.specforgeapi.core.domain.exception.ConfigValidationException;
import java.util.Objects;

public record TypeScriptConfig(BaseConfig base, Structure structure, Enums enums) implements GenerationConfig {

    public TypeScriptConfig {
        if (Objects.isNull(base)) {
            throw new ConfigValidationException("Base configuration is mandatory");
        }
        if (Objects.isNull(structure)) {
            throw new ConfigValidationException("Structure configuration is mandatory");
        }
        if (Objects.isNull(enums)) {
            throw new ConfigValidationException("Enums configuration is mandatory");
        }
    }

    public record Structure(DeclarationStyle style) {
        public Structure {
            if (Objects.isNull(style)) {
                throw new ConfigValidationException("Declaration style is mandatory");
            }
        }

        public enum DeclarationStyle {
            INTERFACE,
            TYPE_ALIAS
        }
    }

    public record Enums(EnumStyle style) {
        public Enums {
            if (Objects.isNull(style)) {
                throw new ConfigValidationException("Enum style is mandatory");
            }
        }

        public enum EnumStyle {
            TS_ENUM,
            UNION_STRING
        }
    }
}
