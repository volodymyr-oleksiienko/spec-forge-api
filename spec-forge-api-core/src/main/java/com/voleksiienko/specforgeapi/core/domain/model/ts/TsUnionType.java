package com.voleksiienko.specforgeapi.core.domain.model.ts;

import com.voleksiienko.specforgeapi.core.common.Asserts;
import com.voleksiienko.specforgeapi.core.domain.exception.TsModelValidationException;
import java.util.List;

public final class TsUnionType implements TsDeclaration {

    private final String name;
    private final List<String> values;

    private TsUnionType(Builder builder) {
        this.name = builder.name;
        this.values = builder.values;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public String getName() {
        return name;
    }

    public List<String> getValues() {
        return values;
    }

    public static class Builder {

        private String name;
        private List<String> values;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder values(List<String> values) {
            this.values = values;
            return this;
        }

        public TsUnionType build() {
            if (Asserts.isBlank(name)) {
                throw new TsModelValidationException("TsUnionType name cannot be blank");
            }
            if (Asserts.isEmpty(values)) {
                throw new TsModelValidationException("TsUnionType must have at least one value");
            }
            this.values = List.copyOf(values);
            return new TsUnionType(this);
        }
    }
}
