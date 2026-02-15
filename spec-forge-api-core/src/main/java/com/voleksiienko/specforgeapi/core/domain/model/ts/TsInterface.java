package com.voleksiienko.specforgeapi.core.domain.model.ts;

import com.voleksiienko.specforgeapi.core.common.Asserts;
import com.voleksiienko.specforgeapi.core.domain.exception.TsModelValidationException;
import java.util.List;

public final class TsInterface implements TsDeclaration {

    private final String name;
    private final List<TsField> fields;

    private TsInterface(Builder builder) {
        this.name = builder.name;
        this.fields = builder.fields;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public String getName() {
        return name;
    }

    public List<TsField> getFields() {
        return fields;
    }

    public static class Builder {

        private String name;
        private List<TsField> fields;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder fields(List<TsField> fields) {
            this.fields = fields;
            return this;
        }

        public TsInterface build() {
            if (Asserts.isBlank(name)) {
                throw new TsModelValidationException("TsInterface name cannot be blank");
            }
            if (Asserts.isEmpty(fields)) {
                throw new TsModelValidationException("TsInterface properties cannot be empty");
            }
            this.fields = List.copyOf(fields);
            return new TsInterface(this);
        }
    }
}
