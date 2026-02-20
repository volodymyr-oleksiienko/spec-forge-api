package com.voleksiienko.specforgeapi.core.application.service.java.inner.type.impl;

import com.voleksiienko.specforgeapi.core.application.annotation.Component;
import com.voleksiienko.specforgeapi.core.application.service.java.inner.type.JavaMappingContext;
import com.voleksiienko.specforgeapi.core.application.service.java.inner.type.JavaTypeReferenceCreator;
import com.voleksiienko.specforgeapi.core.domain.model.java.TypeReference;
import com.voleksiienko.specforgeapi.core.domain.model.spec.type.DoubleSpecType;
import com.voleksiienko.specforgeapi.core.domain.model.spec.type.SpecType;

@Component
public class JavaDoubleTypeReferenceCreator implements JavaTypeReferenceCreator {

    @Override
    public boolean supports(SpecType type) {
        return type instanceof DoubleSpecType;
    }

    @Override
    public TypeReference create(String specPropertyName, SpecType specType, JavaMappingContext ctx) {
        if (ctx.usePrimitiveWrappers()) {
            return TypeReference.builder()
                    .packageName("java.lang")
                    .simpleName("Double")
                    .build();
        } else {
            return TypeReference.builder().simpleName("double").primitive(true).build();
        }
    }
}
