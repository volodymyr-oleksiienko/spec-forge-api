package com.voleksiienko.specforgeapi.core.domain.model.java;

import com.voleksiienko.specforgeapi.core.common.Asserts;
import com.voleksiienko.specforgeapi.core.domain.exception.JavaModelValidationException;
import java.util.List;
import java.util.Objects;

public final class JavaField {

    private final String name;
    private final TypeReference type;
    private final List<JavaAnnotation> annotations;

    private JavaField(Builder builder) {
        this.name = builder.name;
        this.type = builder.type;
        this.annotations = builder.annotations;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getName() {
        return name;
    }

    public TypeReference getType() {
        return type;
    }

    public List<JavaAnnotation> getAnnotations() {
        return annotations;
    }

    public static class Builder {

        private String name;
        private TypeReference type;
        private List<JavaAnnotation> annotations;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder type(TypeReference type) {
            this.type = type;
            return this;
        }

        public Builder annotations(List<JavaAnnotation> annotations) {
            this.annotations = annotations;
            return this;
        }

        public JavaField build() {
            if (Asserts.isBlank(name)) {
                throw new JavaModelValidationException("Field name cannot be blank");
            }
            if (Objects.isNull(type)) {
                throw new JavaModelValidationException("Field type cannot be null");
            }
            this.annotations = Objects.nonNull(annotations) ? List.copyOf(this.annotations) : null;
            return new JavaField(this);
        }
    }
}
