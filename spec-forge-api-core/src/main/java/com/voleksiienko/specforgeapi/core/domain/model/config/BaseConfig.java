package com.voleksiienko.specforgeapi.core.domain.model.config;

import com.voleksiienko.specforgeapi.core.common.Asserts;
import com.voleksiienko.specforgeapi.core.domain.exception.ConfigValidationException;
import java.util.Objects;

public record BaseConfig(Naming naming, Fields fields) {

    public BaseConfig {
        if (Objects.isNull(naming)) {
            throw new ConfigValidationException("Naming configuration is mandatory");
        }
        if (Objects.isNull(fields)) {
            throw new ConfigValidationException("Fields configuration is mandatory");
        }
    }

    public record Naming(String className) {

        public Naming {
            if (Asserts.isBlank(className)) {
                throw new ConfigValidationException("Class name must not be blank");
            }
            if (!className.matches("^[A-Z][a-zA-Z0-9_]*$")) {
                throw new ConfigValidationException(
                        "Class name '%s' is invalid, it must start with an uppercase letter and follow UpperCamelCase"
                                .formatted(className));
            }
        }
    }

    public record Fields(SortType sort) {

        public Fields {
            if (Objects.isNull(sort)) {
                throw new ConfigValidationException("Field sort type is mandatory");
            }
        }

        public enum SortType {
            AS_IS,
            ALPHABETICAL,
            REQUIRED_FIRST
        }
    }
}
