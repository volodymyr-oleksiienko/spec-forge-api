package com.voleksiienko.specforgeapi.core.domain.model.spec;

import com.voleksiienko.specforgeapi.core.common.Asserts;
import com.voleksiienko.specforgeapi.core.domain.exception.SpecModelValidationException;
import com.voleksiienko.specforgeapi.core.domain.model.spec.type.SpecType;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public final class SpecProperty {

    private final String name;
    private final SpecType type;
    private final boolean required;
    private final String description;
    private final boolean deprecated;

    private SpecProperty(Builder builder) {
        this.name = builder.name;
        this.type = builder.type;
        this.required = builder.required;
        this.description = builder.description;
        this.deprecated = builder.deprecated;
    }

    public static void ensurePropertiesUniqueness(List<SpecProperty> properties) {
        Set<String> names = new HashSet<>();
        properties.forEach(property -> {
            if (Objects.isNull(property)) {
                throw new SpecModelValidationException("Object cannot contain null properties");
            }
            if (!names.add(property.getName())) {
                throw new SpecModelValidationException(
                        "Object contains duplicate property name [%s]".formatted(property.getName()));
            }
        });
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getName() {
        return name;
    }

    public SpecType getType() {
        return type;
    }

    public boolean isRequired() {
        return required;
    }

    public String getDescription() {
        return description;
    }

    public boolean isDeprecated() {
        return deprecated;
    }

    public static class Builder {

        private String name;
        private SpecType type;
        private boolean required;
        private String description;
        private boolean deprecated;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder type(SpecType type) {
            this.type = type;
            return this;
        }

        public Builder required(boolean required) {
            this.required = required;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder deprecated(boolean deprecated) {
            this.deprecated = deprecated;
            return this;
        }

        public SpecProperty build() {
            if (Asserts.isBlank(name)) {
                throw new SpecModelValidationException("SpecNode must have name");
            }
            if (Objects.isNull(type)) {
                throw new SpecModelValidationException("SpecNode must have type");
            }
            return new SpecProperty(this);
        }
    }
}
