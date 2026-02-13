package com.voleksiienko.specforgeapi.core.domain.model.java;

import com.voleksiienko.specforgeapi.core.common.Asserts;
import com.voleksiienko.specforgeapi.core.domain.exception.JavaModelValidationException;
import java.util.List;
import java.util.Objects;

public final class JavaEnumConstant {

    private final String name;
    private final List<String> arguments;

    private JavaEnumConstant(Builder builder) {
        this.name = builder.name;
        this.arguments = builder.arguments;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getName() {
        return name;
    }

    public List<String> getArguments() {
        return arguments;
    }

    public static class Builder {

        private String name;
        private List<String> arguments;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder arguments(List<String> arguments) {
            this.arguments = arguments;
            return this;
        }

        public JavaEnumConstant build() {
            if (Asserts.isBlank(name)) {
                throw new JavaModelValidationException("Enum constant name is required");
            }
            this.arguments = Objects.nonNull(arguments) ? List.copyOf(this.arguments) : null;
            return new JavaEnumConstant(this);
        }
    }
}
