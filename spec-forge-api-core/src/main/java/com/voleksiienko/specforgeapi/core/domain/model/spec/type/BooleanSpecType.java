package com.voleksiienko.specforgeapi.core.domain.model.spec.type;

import com.voleksiienko.specforgeapi.core.common.Asserts;
import java.util.List;

public final class BooleanSpecType extends PrimitiveSpecType {

    private BooleanSpecType(Builder builder) {
        super(builder.examples);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private List<String> examples;

        public Builder examples(List<String> examples) {
            this.examples = examples;
            return this;
        }

        public BooleanSpecType build() {
            if (Asserts.isNotEmpty(examples)) {
                examples = List.copyOf(examples);
            }
            return new BooleanSpecType(this);
        }
    }
}
