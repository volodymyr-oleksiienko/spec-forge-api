package com.voleksiienko.specforgeapi.core.domain.model.java;

import com.voleksiienko.specforgeapi.core.common.Asserts;
import com.voleksiienko.specforgeapi.core.domain.exception.JavaModelValidationException;
import java.util.List;
import java.util.Objects;

public final class JavaClass extends JavaType {

    private final boolean isRecord;
    private final List<JavaType> nestedClasses;

    private JavaClass(
            String name,
            List<JavaAnnotation> annotations,
            List<JavaField> fields,
            boolean isRecord,
            List<JavaType> nestedClasses) {
        if (Asserts.isEmpty(fields)) {
            throw new JavaModelValidationException("Java class fields cannot be empty");
        }
        super(name, annotations, fields);
        this.isRecord = isRecord;
        this.nestedClasses = Objects.nonNull(nestedClasses) ? List.copyOf(nestedClasses) : null;
    }

    public static JavaClass createClass(String name, List<JavaAnnotation> annotations, List<JavaField> fields) {
        return new JavaClass(name, annotations, fields, false, null);
    }

    public static JavaClass createClass(
            String name, List<JavaAnnotation> annotations, List<JavaField> fields, List<JavaType> nestedClasses) {
        return new JavaClass(name, annotations, fields, false, nestedClasses);
    }

    public static JavaClass createRecord(String name, List<JavaAnnotation> annotations, List<JavaField> fields) {
        return new JavaClass(name, annotations, fields, true, null);
    }

    public static JavaClass createRecord(
            String name, List<JavaAnnotation> annotations, List<JavaField> fields, List<JavaType> nestedClasses) {
        return new JavaClass(name, annotations, fields, true, nestedClasses);
    }

    public boolean isRecord() {
        return isRecord;
    }

    public List<JavaType> getNestedClasses() {
        return nestedClasses;
    }
}
