package com.voleksiienko.specforgeapi.core.application.service.java.inner.type.impl;

import com.voleksiienko.specforgeapi.core.application.annotation.Component;
import com.voleksiienko.specforgeapi.core.application.service.java.inner.type.JavaMappingContext;
import com.voleksiienko.specforgeapi.core.application.service.java.inner.type.JavaTypeReferenceCreator;
import com.voleksiienko.specforgeapi.core.domain.model.java.TypeReference;
import com.voleksiienko.specforgeapi.core.domain.model.spec.type.BooleanSpecType;
import com.voleksiienko.specforgeapi.core.domain.model.spec.type.SpecType;

@Component
public class JavaBooleanTypeReferenceCreator implements JavaTypeReferenceCreator {

    @Override
    public boolean supports(SpecType type) {
        return type instanceof BooleanSpecType;
    }

    @Override
    public TypeReference create(String specPropertyName, SpecType specType, JavaMappingContext ctx) {
        if (ctx.usePrimitiveWrappers()) {
            return TypeReference.builder()
                    .packageName("java.lang")
                    .simpleName("Boolean")
                    .build();
        } else {
            return TypeReference.builder().simpleName("boolean").primitive(true).build();
        }
    }
}
