package com.voleksiienko.specforgeapi.core.domain.model.spec.type;

import com.voleksiienko.specforgeapi.core.common.Asserts;
import com.voleksiienko.specforgeapi.core.domain.exception.SpecModelValidationException;
import java.util.List;
import java.util.Set;

public final class EnumSpecType extends PrimitiveSpecType {

    private final Set<String> values;

    private EnumSpecType(Builder builder) {
        super(builder.examples);
        this.values = builder.values;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Set<String> getValues() {
        return values;
    }

    public static class Builder {

        private Set<String> values;
        private List<String> examples;

        public Builder values(Set<String> values) {
            this.values = values;
            return this;
        }

        public EnumSpecType build() {
            if (Asserts.isEmpty(values)) {
                throw new SpecModelValidationException("EnumNodeType must have at least one value");
            }
            examples = values.stream().limit(2).toList();
            values = Set.copyOf(values);
            return new EnumSpecType(this);
        }
    }
}
