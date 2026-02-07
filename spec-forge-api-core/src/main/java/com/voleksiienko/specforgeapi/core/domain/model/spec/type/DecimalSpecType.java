package com.voleksiienko.specforgeapi.core.domain.model.spec.type;

import com.voleksiienko.specforgeapi.core.domain.exception.SpecModelValidationException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;

public final class DecimalSpecType extends PrimitiveSpecType {

    public static final int MAX_SCALE = 100;
    private static final BigDecimal DEFAULT_EXAMPLE = new BigDecimal("42");

    private final int scale;
    private final BigDecimal minimum;
    private final BigDecimal maximum;

    private DecimalSpecType(Builder builder) {
        super(builder.examples);
        this.scale = builder.scale;
        this.minimum = builder.minimum;
        this.maximum = builder.maximum;
    }

    public static Builder builder() {
        return new Builder();
    }

    public int getScale() {
        return scale;
    }

    public BigDecimal getMinimum() {
        return minimum;
    }

    public BigDecimal getMaximum() {
        return maximum;
    }

    public static class Builder {

        private int scale = 2;
        private BigDecimal minimum;
        private BigDecimal maximum;
        private List<String> examples;

        public Builder scale(int scale) {
            this.scale = scale;
            return this;
        }

        public Builder minimum(BigDecimal minimum) {
            this.minimum = minimum;
            return this;
        }

        public Builder maximum(BigDecimal maximum) {
            this.maximum = maximum;
            return this;
        }

        public DecimalSpecType build() {
            if (scale < 0) {
                throw new SpecModelValidationException("Scale [%s] cannot be negative".formatted(scale));
            }
            if (scale > MAX_SCALE) {
                throw new SpecModelValidationException(
                        "Scale [%s] exceeds the maximum allowed limit of [%s]".formatted(scale, MAX_SCALE));
            }
            if (Objects.nonNull(minimum) && Objects.nonNull(maximum) && minimum.compareTo(maximum) > 0) {
                throw new SpecModelValidationException(
                        "Minimum [%s] cannot be greater than Maximum [%s]".formatted(minimum, maximum));
            }
            examples = List.of(generatedExample());
            return new DecimalSpecType(this);
        }

        private String generatedExample() {
            BigDecimal exampleValue;
            if (Objects.nonNull(minimum)) {
                exampleValue = minimum;
            } else if (Objects.nonNull(maximum)) {
                exampleValue = maximum;
            } else {
                exampleValue = DEFAULT_EXAMPLE;
            }
            return exampleValue.setScale(scale, RoundingMode.HALF_UP).toPlainString();
        }
    }
}
