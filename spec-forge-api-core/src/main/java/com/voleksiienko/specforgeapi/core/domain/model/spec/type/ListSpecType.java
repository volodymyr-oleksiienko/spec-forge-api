package com.voleksiienko.specforgeapi.core.domain.model.spec.type;

import com.voleksiienko.specforgeapi.core.domain.exception.SpecModelValidationException;
import java.util.Objects;

public final class ListSpecType implements SpecType {

    private final SpecType valueType;
    private final Integer minItems;
    private final Integer maxItems;

    private ListSpecType(Builder builder) {
        this.valueType = builder.valueType;
        this.minItems = builder.minItems;
        this.maxItems = builder.maxItems;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public boolean isObjectStructure() {
        return valueType.isObjectStructure();
    }

    public SpecType getValueType() {
        return valueType;
    }

    public Integer getMinItems() {
        return minItems;
    }

    public Integer getMaxItems() {
        return maxItems;
    }

    public static class Builder {

        private SpecType valueType;
        private Integer minItems;
        private Integer maxItems;

        public Builder valueType(SpecType valueType) {
            this.valueType = valueType;
            return this;
        }

        public Builder minItems(Integer minItems) {
            this.minItems = minItems;
            return this;
        }

        public Builder maxItems(Integer maxItems) {
            this.maxItems = maxItems;
            return this;
        }

        public ListSpecType build() {
            if (Objects.nonNull(minItems) && minItems < 0) {
                throw new SpecModelValidationException("MinItems [%s] cannot be negative".formatted(minItems));
            }
            if (Objects.nonNull(maxItems) && maxItems < 0) {
                throw new SpecModelValidationException("MaxItems [%s] cannot be negative".formatted(maxItems));
            }
            if (Objects.nonNull(minItems) && Objects.nonNull(maxItems) && minItems > maxItems) {
                throw new SpecModelValidationException(
                        "MinItems [%s] cannot be greater than MaxItems [%s]".formatted(minItems, maxItems));
            }
            switch (valueType) {
                case null -> throw new SpecModelValidationException("ListSpecType must specify valueType");

                case MapSpecType mapNodeType ->
                    throw new SpecModelValidationException(
                            "Lists cannot contain Maps directly, structure 'List<Map>' is forbidden");

                case ListSpecType innerList -> {
                    if (innerList.getValueType() instanceof ListSpecType) {
                        throw new SpecModelValidationException(
                                "Max nesting depth exceeded, 'List<List<List<...>>>' is forbidden");
                    }

                    if (innerList.getValueType() instanceof ObjectSpecType) {
                        throw new SpecModelValidationException(
                                "Objects cannot be nested in inner lists, 'List<List<Object>>' is forbidden");
                    }
                }
                default -> {
                    // do nothing
                }
            }
            return new ListSpecType(this);
        }
    }
}
