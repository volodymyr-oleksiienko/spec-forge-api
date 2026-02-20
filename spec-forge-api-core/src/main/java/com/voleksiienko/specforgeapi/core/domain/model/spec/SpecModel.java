package com.voleksiienko.specforgeapi.core.domain.model.spec;

import static com.voleksiienko.specforgeapi.core.domain.model.spec.SpecProperty.ensurePropertiesUniqueness;

import com.voleksiienko.specforgeapi.core.common.Asserts;
import com.voleksiienko.specforgeapi.core.domain.exception.SpecModelValidationException;
import java.util.List;
import java.util.Objects;

public final class SpecModel {

    private final WrapperType wrapperType;
    private final List<SpecProperty> properties;

    private SpecModel(Builder builder) {
        this.wrapperType = builder.wrapperType;
        this.properties = builder.properties;
    }

    public static Builder builder() {
        return new Builder();
    }

    public WrapperType getWrapperType() {
        return wrapperType;
    }

    public List<SpecProperty> getProperties() {
        return properties;
    }

    public enum WrapperType {
        OBJECT,
        LIST
    }

    public static class Builder {

        private WrapperType wrapperType;
        private List<SpecProperty> properties;

        public Builder wrapperType(WrapperType wrapperType) {
            this.wrapperType = wrapperType;
            return this;
        }

        public Builder properties(List<SpecProperty> properties) {
            this.properties = properties;
            return this;
        }

        public SpecModel build() {
            if (Objects.isNull(wrapperType)) {
                throw new SpecModelValidationException("wrapperType is required");
            }
            if (Asserts.isEmpty(properties)) {
                throw new SpecModelValidationException("SpecModel must contain at least one node");
            }
            ensurePropertiesUniqueness(properties);
            properties = List.copyOf(properties);
            return new SpecModel(this);
        }
    }
}
