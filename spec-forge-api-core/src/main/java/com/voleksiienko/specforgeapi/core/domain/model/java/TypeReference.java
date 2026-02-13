package com.voleksiienko.specforgeapi.core.domain.model.java;

import com.voleksiienko.specforgeapi.core.common.Asserts;
import com.voleksiienko.specforgeapi.core.domain.exception.JavaModelValidationException;
import java.util.List;
import java.util.Objects;

public final class TypeReference {

    private final String packageName;
    private final String simpleName;
    private final List<TypeReference> genericArguments;
    private final boolean isPrimitive;

    private TypeReference(Builder builder) {
        this.packageName = builder.packageName;
        this.simpleName = builder.simpleName;
        this.genericArguments = builder.genericArguments;
        this.isPrimitive = builder.isPrimitive;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getPackageName() {
        return packageName;
    }

    public String getSimpleName() {
        return simpleName;
    }

    public List<TypeReference> getGenericArguments() {
        return genericArguments;
    }

    public boolean isPrimitive() {
        return isPrimitive;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TypeReference that = (TypeReference) o;
        return isPrimitive == that.isPrimitive
                && Objects.equals(packageName, that.packageName)
                && Objects.equals(simpleName, that.simpleName)
                && Objects.equals(genericArguments, that.genericArguments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(packageName, simpleName, genericArguments, isPrimitive);
    }

    public static class Builder {

        private String packageName;
        private String simpleName;
        private List<TypeReference> genericArguments;
        private boolean isPrimitive;

        public Builder packageName(String packageName) {
            this.packageName = packageName;
            return this;
        }

        public Builder simpleName(String simpleName) {
            this.simpleName = simpleName;
            return this;
        }

        public Builder primitive(boolean isPrimitive) {
            this.isPrimitive = isPrimitive;
            return this;
        }

        public Builder genericArguments(List<TypeReference> genericArguments) {
            this.genericArguments = genericArguments;
            return this;
        }

        public TypeReference build() {
            if (Asserts.isBlank(simpleName)) {
                throw new JavaModelValidationException("Type simpleName cannot be blank");
            }
            if (isPrimitive && Objects.nonNull(packageName)) {
                throw new JavaModelValidationException("Primitive types must not have a package name");
            }
            this.genericArguments = Objects.nonNull(genericArguments) ? List.copyOf(this.genericArguments) : null;
            return new TypeReference(this);
        }
    }
}
