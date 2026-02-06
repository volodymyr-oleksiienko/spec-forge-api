package com.voleksiienko.specforgeapi.core.domain.model.spec.type;

import com.voleksiienko.specforgeapi.core.domain.exception.SpecModelValidationException;
import java.util.Objects;

public final class MapSpecType implements SpecType {

    private final SpecType keyType;
    private final SpecType valueType;

    private MapSpecType(Builder builder) {
        this.keyType = builder.keyType;
        this.valueType = builder.valueType;
    }

    public static Builder builder() {
        return new Builder();
    }

    public SpecType getKeyType() {
        return keyType;
    }

    public SpecType getValueType() {
        return valueType;
    }

    @Override
    public boolean isObjectStructure() {
        return valueType.isObjectStructure();
    }

    public static class Builder {

        private SpecType keyType;
        private SpecType valueType;

        public Builder keyType(SpecType keyType) {
            this.keyType = keyType;
            return this;
        }

        public Builder valueType(SpecType valueType) {
            this.valueType = valueType;
            return this;
        }

        public MapSpecType build() {
            if (Objects.isNull(keyType) || Objects.isNull(valueType)) {
                throw new SpecModelValidationException("MapSpecType must specify both keyType and valueType");
            }
            if (!isPrimitiveType(keyType)) {
                throw new SpecModelValidationException(
                        "Map key must be primitive types (String, Number, Boolean, Enum), found: %s"
                                .formatted(keyType.getClass().getSimpleName()));
            }
            if (valueType instanceof MapSpecType) {
                throw new SpecModelValidationException(
                        "Map cannot contain other Map as value, nesting Maps is forbidden");
            }
            if (valueType instanceof ListSpecType listNode && listNode.getValueType() instanceof ListSpecType) {
                throw new SpecModelValidationException(
                        "Map value cannot be nested Lists, 'Map<K, List<List<...>>>' is forbidden");
            }
            return new MapSpecType(this);
        }

        private boolean isPrimitiveType(SpecType type) {
            return type instanceof StringSpecType
                    || type instanceof IntegerSpecType
                    || type instanceof DoubleSpecType
                    || type instanceof DecimalSpecType
                    || type instanceof BooleanSpecType
                    || type instanceof EnumSpecType;
        }
    }
}
