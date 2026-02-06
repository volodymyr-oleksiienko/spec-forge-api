package com.voleksiienko.specforgeapi.core.domain.model.spec.type;

import com.voleksiienko.specforgeapi.core.domain.exception.SpecModelValidationException;
import java.util.Objects;

public final class DoubleSpecType implements SpecType {

    private final Double minimum;
    private final Double maximum;

    private DoubleSpecType(Builder builder) {
        this.minimum = builder.minimum;
        this.maximum = builder.maximum;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public boolean isObjectStructure() {
        return false;
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

        public Builder minimum(Double minimum) {
            this.minimum = minimum;
            return this;
        }

        public Builder maximum(Double maximum) {
            this.maximum = maximum;
            return this;
        }

        public DoubleSpecType build() {
            if (Objects.nonNull(minimum) && Objects.nonNull(maximum) && minimum > maximum) {
                throw new SpecModelValidationException(
                        "Minimum [%s] cannot be greater than Maximum [%s]".formatted(minimum, maximum));
            }
            return new DoubleSpecType(this);
        }
    }
}
