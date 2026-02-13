package com.voleksiienko.specforgeapi.core.domain.model.java;

import com.voleksiienko.specforgeapi.core.common.Asserts;
import com.voleksiienko.specforgeapi.core.domain.exception.JavaModelValidationException;
import java.util.List;
import java.util.Objects;

public abstract sealed class JavaType permits JavaClass, JavaEnum {

    protected final String name;
    protected final List<JavaAnnotation> annotations;
    protected final List<JavaField> fields;

    protected JavaType(String name, List<JavaAnnotation> annotations, List<JavaField> fields) {
        if (Asserts.isBlank(name)) {
            throw new JavaModelValidationException("JavaType name cannot be blank");
        }
        this.name = name;
        this.annotations = Objects.nonNull(annotations) ? List.copyOf(annotations) : null;
        this.fields = Objects.nonNull(fields) ? List.copyOf(fields) : null;
    }

    public String getName() {
        return name;
    }

    public List<JavaAnnotation> getAnnotations() {
        return annotations;
    }

    public List<JavaField> getFields() {
        return fields;
    }
}
