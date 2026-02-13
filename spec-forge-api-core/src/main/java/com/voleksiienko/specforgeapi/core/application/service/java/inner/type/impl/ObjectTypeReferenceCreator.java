package com.voleksiienko.specforgeapi.core.application.service.java.inner.type.impl;

import com.voleksiienko.specforgeapi.core.application.annotation.Component;
import com.voleksiienko.specforgeapi.core.application.service.java.inner.JavaClassFactory;
import com.voleksiienko.specforgeapi.core.application.service.java.inner.type.ClassNameCreator;
import com.voleksiienko.specforgeapi.core.application.service.java.inner.type.MappingContext;
import com.voleksiienko.specforgeapi.core.application.service.java.inner.type.TypeReferenceCreator;
import com.voleksiienko.specforgeapi.core.domain.model.java.TypeReference;
import com.voleksiienko.specforgeapi.core.domain.model.spec.type.ObjectSpecType;
import com.voleksiienko.specforgeapi.core.domain.model.spec.type.SpecType;

@Component
public class ObjectTypeReferenceCreator implements TypeReferenceCreator {

    private final JavaClassFactory javaClassFactory;
    private final ClassNameCreator classNameCreator;

    public ObjectTypeReferenceCreator(JavaClassFactory javaClassFactory, ClassNameCreator classNameCreator) {
        this.javaClassFactory = javaClassFactory;
        this.classNameCreator = classNameCreator;
    }

    @Override
    public boolean supports(SpecType type) {
        return type instanceof ObjectSpecType;
    }

    @Override
    public TypeReference create(String specPropertyName, SpecType specType, MappingContext ctx) {
        String className = classNameCreator.create(specPropertyName, ctx);
        ctx.javaTypes().add(javaClassFactory.mapToClass(className, ((ObjectSpecType) specType).getChildren(), ctx));
        return TypeReference.builder().simpleName(className).build();
    }
}
