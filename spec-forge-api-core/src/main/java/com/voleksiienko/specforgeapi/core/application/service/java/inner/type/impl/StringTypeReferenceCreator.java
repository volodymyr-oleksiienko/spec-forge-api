package com.voleksiienko.specforgeapi.core.application.service.java.inner.type.impl;

import com.voleksiienko.specforgeapi.core.application.annotation.Component;
import com.voleksiienko.specforgeapi.core.application.service.java.inner.type.MappingContext;
import com.voleksiienko.specforgeapi.core.application.service.java.inner.type.TypeReferenceCreator;
import com.voleksiienko.specforgeapi.core.domain.model.java.TypeReference;
import com.voleksiienko.specforgeapi.core.domain.model.spec.type.SpecType;
import com.voleksiienko.specforgeapi.core.domain.model.spec.type.StringSpecType;

@Component
public class StringTypeReferenceCreator implements TypeReferenceCreator {

    @Override
    public boolean supports(SpecType type) {
        return type instanceof StringSpecType;
    }

    @Override
    public TypeReference create(String specPropertyName, SpecType specType, MappingContext ctx) {
        StringSpecType type = (StringSpecType) specType;
        if (type.getFormat() == StringSpecType.StringTypeFormat.UUID) {
            return TypeReference.builder()
                    .packageName("java.util")
                    .simpleName("UUID")
                    .build();
        }
        return TypeReference.builder()
                .packageName("java.lang")
                .simpleName("String")
                .build();
    }
}
