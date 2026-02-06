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
    private final List<SpecProperty> children;
    private final String description;
    private final List<String> examples;
    private final boolean deprecated;

    private SpecProperty(Builder builder) {
        this.name = builder.name;
        this.type = builder.type;
        this.required = builder.required;
        this.description = builder.description;
        this.examples = builder.examples;
        this.children = builder.children;
        this.deprecated = builder.deprecated;
    }

    public static void ensurePropertiesUniqueness(List<SpecProperty> properties, String containerContext) {
        Set<String> names = new HashSet<>();
        properties.forEach(property -> {
            if (Objects.isNull(property)) {
                throw new SpecModelValidationException(
                        "[%s] cannot contain null properties".formatted(containerContext));
            }
            if (!names.add(property.getName())) {
                throw new SpecModelValidationException(
                        "[%s] contains duplicate property name [%s]".formatted(containerContext, property.getName()));
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

    public List<String> getExamples() {
        return examples;
    }

    public List<SpecProperty> getChildren() {
        return children;
    }

    public boolean isDeprecated() {
        return deprecated;
    }

    public static class Builder {

        private String name;
        private SpecType type;
        private boolean required;
        private List<SpecProperty> children;
        private String description;
        private List<String> examples;
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

        public Builder children(List<SpecProperty> children) {
            this.children = children;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder examples(List<String> examples) {
            this.examples = examples;
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
            validateChildrenStructure();
            this.children = Objects.isNull(children) ? List.of() : List.copyOf(children);
            this.examples = Objects.isNull(examples) ? List.of() : List.copyOf(examples);
            return new SpecProperty(this);
        }

        private void validateChildrenStructure() {
            if (type.isObjectStructure()) {
                if (Asserts.isEmpty(children)) {
                    throw new SpecModelValidationException(
                            "Node '%s' of type %s must have children to define its structure"
                                    .formatted(name, type.getClass().getSimpleName()));
                }
                ensurePropertiesUniqueness(children, name);
            } else {
                if (Asserts.isNotEmpty(children)) {
                    throw new SpecModelValidationException("Node '%s' of type %s is primitive and cannot have children"
                            .formatted(name, type.getClass().getSimpleName()));
                }
            }
        }
    }
}
