package com.voleksiienko.specforgeapi.core.application.service.java.inner.type.impl;

import com.voleksiienko.specforgeapi.core.application.annotation.Component;
import com.voleksiienko.specforgeapi.core.application.service.java.inner.type.JavaMappingContext;
import com.voleksiienko.specforgeapi.core.application.service.java.inner.type.JavaTypeReferenceCreator;
import com.voleksiienko.specforgeapi.core.domain.model.java.TypeReference;
import com.voleksiienko.specforgeapi.core.domain.model.spec.type.DateSpecType;
import com.voleksiienko.specforgeapi.core.domain.model.spec.type.SpecType;

@Component
public class JavaDateTypeReferenceCreator implements JavaTypeReferenceCreator {

    @Override
    public boolean supports(SpecType type) {
        return type instanceof DateSpecType;
    }

    @Override
    public TypeReference create(String specPropertyName, SpecType specType, JavaMappingContext ctx) {
        return TypeReference.builder()
                .packageName("java.time")
                .simpleName("LocalDate")
                .build();
    }
}
