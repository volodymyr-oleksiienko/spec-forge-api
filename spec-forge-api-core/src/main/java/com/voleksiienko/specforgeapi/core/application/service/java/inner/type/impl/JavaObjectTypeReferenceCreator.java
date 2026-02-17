package com.voleksiienko.specforgeapi.core.application.service.java.inner.type.impl;

import com.voleksiienko.specforgeapi.core.application.annotation.Component;
import com.voleksiienko.specforgeapi.core.application.service.java.inner.JavaClassFactory;
import com.voleksiienko.specforgeapi.core.application.service.java.inner.type.JavaClassNameCreator;
import com.voleksiienko.specforgeapi.core.application.service.java.inner.type.JavaMappingContext;
import com.voleksiienko.specforgeapi.core.application.service.java.inner.type.JavaTypeReferenceCreator;
import com.voleksiienko.specforgeapi.core.domain.model.java.TypeReference;
import com.voleksiienko.specforgeapi.core.domain.model.spec.type.ObjectSpecType;
import com.voleksiienko.specforgeapi.core.domain.model.spec.type.SpecType;

@Component
public class JavaObjectTypeReferenceCreator implements JavaTypeReferenceCreator {

    private final JavaClassFactory javaClassFactory;
    private final JavaClassNameCreator javaClassNameCreator;

    public JavaObjectTypeReferenceCreator(
            JavaClassFactory javaClassFactory, JavaClassNameCreator javaClassNameCreator) {
        this.javaClassFactory = javaClassFactory;
        this.javaClassNameCreator = javaClassNameCreator;
    }

    @Override
    public boolean supports(SpecType type) {
        return type instanceof ObjectSpecType;
    }

    @Override
    public TypeReference create(String specPropertyName, SpecType specType, JavaMappingContext ctx) {
        String className = javaClassNameCreator.create(specPropertyName, ctx);
        ctx.javaTypes().add(javaClassFactory.mapToClass(className, ((ObjectSpecType) specType).getChildren(), ctx));
        return TypeReference.builder().simpleName(className).build();
    }
}
