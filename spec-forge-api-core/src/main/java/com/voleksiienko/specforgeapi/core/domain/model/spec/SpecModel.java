package com.voleksiienko.specforgeapi.core.domain.model.spec;

import static com.voleksiienko.specforgeapi.core.domain.model.spec.SpecProperty.ensurePropertiesUniqueness;

import com.voleksiienko.specforgeapi.core.common.Asserts;
import com.voleksiienko.specforgeapi.core.domain.exception.SpecModelValidationException;
import java.util.List;
import java.util.Objects;

public final class SpecModel {

    private final WrapperType wrapperType;
    private final List<SpecProperty> specProperties;

    private SpecModel(Builder builder) {
        this.wrapperType = builder.wrapperType;
        this.specProperties = builder.specProperties;
    }

    public static Builder builder() {
        return new Builder();
    }

    public WrapperType getWrapperType() {
        return wrapperType;
    }

    public List<SpecProperty> getProperties() {
        return specProperties;
    }

    public enum WrapperType {
        OBJECT,
        LIST
    }

    public static class Builder {

        private WrapperType wrapperType;
        private List<SpecProperty> specProperties;

        public Builder wrapperType(WrapperType wrapperType) {
            this.wrapperType = wrapperType;
            return this;
        }

        public Builder specProperties(List<SpecProperty> specProperties) {
            this.specProperties = specProperties;
            return this;
        }

        public SpecModel build() {
            if (Objects.isNull(wrapperType)) {
                throw new SpecModelValidationException("wrapperType is required");
            }
            if (Asserts.isEmpty(specProperties)) {
                throw new SpecModelValidationException("SpecModel must contain at least one node");
            }
            ensurePropertiesUniqueness(specProperties, "SpecModel");
            specProperties = List.copyOf(specProperties);
            return new SpecModel(this);
        }
    }
}
