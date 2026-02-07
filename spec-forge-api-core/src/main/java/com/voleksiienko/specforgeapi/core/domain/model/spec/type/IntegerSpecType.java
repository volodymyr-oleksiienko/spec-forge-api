package com.voleksiienko.specforgeapi.core.domain.model.spec.type;

import com.voleksiienko.specforgeapi.core.domain.exception.SpecModelValidationException;
import java.util.List;
import java.util.Objects;

public final class IntegerSpecType extends PrimitiveSpecType {

    private static final Long DEFAULT_EXAMPLE = 56L;

    private final Long minimum;
    private final Long maximum;

    private IntegerSpecType(Builder builder) {
        super(builder.examples);
        this.minimum = builder.minimum;
        this.maximum = builder.maximum;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Long getMinimum() {
        return minimum;
    }

    public Long getMaximum() {
        return maximum;
    }

    public static class Builder {

        private Long minimum;
        private Long maximum;
        private List<String> examples;

        public Builder minimum(Long minimum) {
            this.minimum = minimum;
            return this;
        }

        public Builder maximum(Long maximum) {
            this.maximum = maximum;
            return this;
        }

        public IntegerSpecType build() {
            if (Objects.nonNull(minimum) && Objects.nonNull(maximum) && minimum > maximum) {
                throw new SpecModelValidationException(
                        "Minimum [%s] cannot be greater than Maximum [%s]".formatted(minimum, maximum));
            }
            examples = List.of(generateExample());
            return new IntegerSpecType(this);
        }

        private String generateExample() {
            Long exampleValue;
            if (Objects.nonNull(minimum)) {
                exampleValue = minimum;
            } else if (Objects.nonNull(maximum)) {
                exampleValue = maximum;
            } else {
                exampleValue = DEFAULT_EXAMPLE;
            }
            return String.valueOf(exampleValue);
        }
    }
}
