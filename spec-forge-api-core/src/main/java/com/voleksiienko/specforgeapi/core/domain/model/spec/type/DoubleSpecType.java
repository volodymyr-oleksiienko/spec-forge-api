package com.voleksiienko.specforgeapi.core.domain.model.spec.type;

import com.voleksiienko.specforgeapi.core.common.Asserts;
import com.voleksiienko.specforgeapi.core.domain.exception.SpecModelValidationException;
import java.util.List;
import java.util.Objects;

public final class DoubleSpecType extends PrimitiveSpecType {

    private final Double minimum;
    private final Double maximum;

    private DoubleSpecType(Builder builder) {
        super(builder.examples);
        this.minimum = builder.minimum;
        this.maximum = builder.maximum;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Double getMinimum() {
        return minimum;
    }

    public Double getMaximum() {
        return maximum;
    }

    public static class Builder {

        private Double minimum;
        private Double maximum;
        private List<String> examples;

        public Builder minimum(Double minimum) {
            this.minimum = minimum;
            return this;
        }

        public Builder maximum(Double maximum) {
            this.maximum = maximum;
            return this;
        }

        public Builder examples(List<String> examples) {
            this.examples = examples;
            return this;
        }

        public DoubleSpecType build() {
            if (Objects.nonNull(minimum) && Objects.nonNull(maximum) && minimum > maximum) {
                throw new SpecModelValidationException(
                        "Minimum [%s] cannot be greater than Maximum [%s]".formatted(minimum, maximum));
            }
            if (Asserts.isNotEmpty(examples)) {
                examples = List.copyOf(examples);
            }
            return new DoubleSpecType(this);
        }
    }
}
