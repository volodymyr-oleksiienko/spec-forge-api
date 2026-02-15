package com.voleksiienko.specforgeapi.core.domain.model.ts;

import com.voleksiienko.specforgeapi.core.common.Asserts;
import com.voleksiienko.specforgeapi.core.domain.exception.TsModelValidationException;

public final class TsEnumConstant {

    private final String key;
    private final String value;

    private TsEnumConstant(Builder builder) {
        this.key = builder.key;
        this.value = builder.value;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public static class Builder {

        private String key;
        private String value;

        public Builder key(String key) {
            this.key = key;
            return this;
        }

        public Builder value(String value) {
            this.value = value;
            return this;
        }

        public TsEnumConstant build() {
            if (Asserts.isBlank(key)) {
                throw new TsModelValidationException("TsEnumConstant key cannot be blank");
            }
            if (Asserts.isBlank(value)) {
                throw new TsModelValidationException("TsEnumConstant value cannot be blank");
            }
            return new TsEnumConstant(this);
        }
    }
}
