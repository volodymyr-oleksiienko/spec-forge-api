package com.voleksiienko.specforgeapi.core.application.service.java.inner.type.impl;

import com.voleksiienko.specforgeapi.core.application.annotation.Component;
import com.voleksiienko.specforgeapi.core.application.service.java.inner.JavaEnumFactory;
import com.voleksiienko.specforgeapi.core.application.service.java.inner.type.ClassNameCreator;
import com.voleksiienko.specforgeapi.core.application.service.java.inner.type.MappingContext;
import com.voleksiienko.specforgeapi.core.application.service.java.inner.type.TypeReferenceCreator;
import com.voleksiienko.specforgeapi.core.domain.model.java.TypeReference;
import com.voleksiienko.specforgeapi.core.domain.model.spec.type.EnumSpecType;
import com.voleksiienko.specforgeapi.core.domain.model.spec.type.SpecType;

@Component
public class EnumTypeReferenceCreator implements TypeReferenceCreator {

    private final JavaEnumFactory javaEnumFactory;
    private final ClassNameCreator classNameCreator;

    public EnumTypeReferenceCreator(JavaEnumFactory javaEnumFactory, ClassNameCreator classNameCreator) {
        this.javaEnumFactory = javaEnumFactory;
        this.classNameCreator = classNameCreator;
    }

    @Override
    public boolean supports(SpecType type) {
        return type instanceof EnumSpecType;
    }

    @Override
    public TypeReference create(String specPropertyName, SpecType specType, MappingContext ctx) {
        String className = classNameCreator.create(specPropertyName, ctx);
        ctx.javaTypes().add(javaEnumFactory.convertToEnum(className, (EnumSpecType) specType));
        return TypeReference.builder().simpleName(className).build();
    }
}
