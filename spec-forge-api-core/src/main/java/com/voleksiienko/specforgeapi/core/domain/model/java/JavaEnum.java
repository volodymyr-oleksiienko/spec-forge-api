package com.voleksiienko.specforgeapi.core.domain.model.java;

import com.voleksiienko.specforgeapi.core.common.Asserts;
import com.voleksiienko.specforgeapi.core.domain.exception.JavaModelValidationException;
import java.util.List;

public final class JavaEnum extends JavaType {

    private final List<JavaEnumConstant> constants;

    private JavaEnum(
            String name, List<JavaAnnotation> annotations, List<JavaField> fields, List<JavaEnumConstant> constants) {
        super(name, annotations, fields);
        if (Asserts.isEmpty(constants)) {
            throw new JavaModelValidationException("JavaEnum must have at least one constant");
        }
        this.constants = List.copyOf(constants);
    }

    public static JavaEnum of(
            String name, List<JavaAnnotation> annotations, List<JavaField> fields, List<JavaEnumConstant> constants) {
        return new JavaEnum(name, annotations, fields, constants);
    }

    public List<JavaEnumConstant> getConstants() {
        return constants;
    }
}
