package com.voleksiienko.specforgeapi.core.domain.model.spec.type;

import static com.voleksiienko.specforgeapi.core.domain.model.spec.SpecProperty.ensurePropertiesUniqueness;

import com.voleksiienko.specforgeapi.core.common.Asserts;
import com.voleksiienko.specforgeapi.core.domain.exception.SpecModelValidationException;
import com.voleksiienko.specforgeapi.core.domain.model.spec.SpecProperty;
import java.util.ArrayList;
import java.util.List;

public final class ObjectSpecType implements SpecType {

    private final List<SpecProperty> children;

    private ObjectSpecType(Builder builder) {
        this.children = builder.children;
    }

    public static Builder builder() {
        return new Builder();
    }

    public List<SpecProperty> getChildren() {
        return children;
    }

    @Override
    public boolean isObjectStructure() {
        return true;
    }

    public static final class Builder {

        private List<SpecProperty> children = new ArrayList<>();

        public Builder children(List<SpecProperty> children) {
            this.children = children;
            return this;
        }

        public ObjectSpecType build() {
            if (Asserts.isEmpty(this.children)) {
                throw new SpecModelValidationException("Object must have children to define its structure");
            }
            ensurePropertiesUniqueness(children);
            children = List.copyOf(children);
            return new ObjectSpecType(this);
        }
    }
}
