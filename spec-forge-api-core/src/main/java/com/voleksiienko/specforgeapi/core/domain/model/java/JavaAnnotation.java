package com.voleksiienko.specforgeapi.core.domain.model.java;

import com.voleksiienko.specforgeapi.core.common.Asserts;
import com.voleksiienko.specforgeapi.core.domain.exception.JavaModelValidationException;
import java.util.Map;

public final class JavaAnnotation {

    private final String packageName;
    private final String simpleName;
    private final Map<String, String> attributes;

    private JavaAnnotation(Builder builder) {
        this.packageName = builder.packageName;
        this.simpleName = builder.simpleName;
        this.attributes = builder.attributes;
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

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public static class Builder {

        private String packageName;
        private String simpleName;
        private Map<String, String> attributes;

        public Builder packageName(String packageName) {
            this.packageName = packageName;
            return this;
        }

        public Builder simpleName(String simpleName) {
            this.simpleName = simpleName;
            return this;
        }

        public Builder attributes(Map<String, String> attributes) {
            this.attributes = attributes;
            return this;
        }

        public JavaAnnotation build() {
            if (Asserts.isBlank(simpleName)) {
                throw new JavaModelValidationException("Annotation simpleName must be not blank");
            }
            if (Asserts.isBlank(packageName)) {
                throw new JavaModelValidationException("Annotation packageName must be not blank");
            }
            if (Asserts.isNotEmpty(attributes)) {
                this.attributes = Map.copyOf(this.attributes);
            }
            return new JavaAnnotation(this);
        }
    }
}
