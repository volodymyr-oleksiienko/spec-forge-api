package com.voleksiienko.specforgeapi.core.domain.model.ts;

import com.voleksiienko.specforgeapi.core.common.Asserts;
import com.voleksiienko.specforgeapi.core.domain.exception.TsModelValidationException;
import java.util.List;

public final class TsTypeReference {

    private final String typeName;
    private final List<TsTypeReference> genericArguments;

    private TsTypeReference(Builder builder) {
        this.typeName = builder.typeName;
        this.genericArguments = builder.genericArguments;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getTypeName() {
        return typeName;
    }

    public List<TsTypeReference> getGenericArguments() {
        return genericArguments;
    }

    public static class Builder {

        private String typeName;
        private List<TsTypeReference> genericArguments;

        public Builder typeName(String typeName) {
            this.typeName = typeName;
            return this;
        }

        public Builder genericArguments(List<TsTypeReference> genericArguments) {
            this.genericArguments = genericArguments;
            return this;
        }

        public TsTypeReference build() {
            if (Asserts.isBlank(typeName)) {
                throw new TsModelValidationException("TsTypeReference typeName cannot be blank");
            }
            this.genericArguments = Asserts.isNotEmpty(genericArguments) ? List.copyOf(genericArguments) : null;
            return new TsTypeReference(this);
        }
    }
}
