package com.voleksiienko.specforgeapi.core.application.service.java.inner.type.impl;

import com.voleksiienko.specforgeapi.core.application.annotation.Component;
import com.voleksiienko.specforgeapi.core.application.service.java.inner.JavaEnumFactory;
import com.voleksiienko.specforgeapi.core.application.service.java.inner.type.JavaClassNameCreator;
import com.voleksiienko.specforgeapi.core.application.service.java.inner.type.JavaMappingContext;
import com.voleksiienko.specforgeapi.core.application.service.java.inner.type.JavaTypeReferenceCreator;
import com.voleksiienko.specforgeapi.core.domain.model.java.TypeReference;
import com.voleksiienko.specforgeapi.core.domain.model.spec.type.EnumSpecType;
import com.voleksiienko.specforgeapi.core.domain.model.spec.type.SpecType;

@Component
public class JavaEnumTypeReferenceCreator implements JavaTypeReferenceCreator {

    private final JavaEnumFactory javaEnumFactory;
    private final JavaClassNameCreator javaClassNameCreator;

    public JavaEnumTypeReferenceCreator(JavaEnumFactory javaEnumFactory, JavaClassNameCreator javaClassNameCreator) {
        this.javaEnumFactory = javaEnumFactory;
        this.javaClassNameCreator = javaClassNameCreator;
    }

    @Override
    public boolean supports(SpecType type) {
        return type instanceof EnumSpecType;
    }

    @Override
    public TypeReference create(String specPropertyName, SpecType specType, JavaMappingContext ctx) {
        String className = javaClassNameCreator.create(specPropertyName, ctx);
        ctx.javaTypes().add(javaEnumFactory.convertToEnum(className, (EnumSpecType) specType));
        return TypeReference.builder().simpleName(className).build();
    }
}
