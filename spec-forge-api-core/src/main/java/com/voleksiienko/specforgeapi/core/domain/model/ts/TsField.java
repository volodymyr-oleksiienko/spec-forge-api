package com.voleksiienko.specforgeapi.core.domain.model.ts;

import com.voleksiienko.specforgeapi.core.common.Asserts;
import com.voleksiienko.specforgeapi.core.domain.exception.TsModelValidationException;
import java.util.Objects;

public final class TsField {

    private final String name;
    private final TsTypeReference type;
    private final boolean isOptional;

    private TsField(Builder builder) {
        this.name = builder.name;
        this.type = builder.type;
        this.isOptional = builder.isOptional;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getName() {
        return name;
    }

    public TsTypeReference getType() {
        return type;
    }

    public boolean isOptional() {
        return isOptional;
    }

    public static class Builder {

        private String name;
        private TsTypeReference type;
        private boolean isOptional;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder type(TsTypeReference type) {
            this.type = type;
            return this;
        }

        public Builder optional(boolean isOptional) {
            this.isOptional = isOptional;
            return this;
        }

        public TsField build() {
            if (Asserts.isBlank(name)) {
                throw new TsModelValidationException("TsField name cannot be blank");
            }
            if (Objects.isNull(type)) {
                throw new TsModelValidationException("TsField type cannot be null");
            }
            return new TsField(this);
        }
    }
}
