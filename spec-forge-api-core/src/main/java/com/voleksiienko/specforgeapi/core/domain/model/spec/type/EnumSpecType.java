package com.voleksiienko.specforgeapi.core.domain.model.spec.type;

import com.voleksiienko.specforgeapi.core.common.Asserts;
import com.voleksiienko.specforgeapi.core.domain.exception.SpecModelValidationException;
import java.util.Set;

public final class EnumSpecType implements SpecType {

    private final Set<String> values;

    private EnumSpecType(Builder builder) {
        this.values = builder.values;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public boolean isObjectStructure() {
        return false;
    }

    public Set<String> getValues() {
        return values;
    }

    public static class Builder {

        private Set<String> values;

        public Builder values(Set<String> values) {
            this.values = values;
            return this;
        }

        public EnumSpecType build() {
            if (Asserts.isEmpty(values)) {
                throw new SpecModelValidationException("EnumNodeType must have at least one value");
            }
            values = Set.copyOf(values);
            return new EnumSpecType(this);
        }
    }
}
