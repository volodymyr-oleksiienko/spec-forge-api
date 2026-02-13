package com.voleksiienko.specforgeapi.core.application.service.java.inner.type.impl;

import com.voleksiienko.specforgeapi.core.application.annotation.Component;
import com.voleksiienko.specforgeapi.core.application.service.java.inner.type.MappingContext;
import com.voleksiienko.specforgeapi.core.application.service.java.inner.type.TypeReferenceCreator;
import com.voleksiienko.specforgeapi.core.domain.model.java.TypeReference;
import com.voleksiienko.specforgeapi.core.domain.model.spec.type.DateSpecType;
import com.voleksiienko.specforgeapi.core.domain.model.spec.type.SpecType;

@Component
public class DateTypeReferenceCreator implements TypeReferenceCreator {

    @Override
    public boolean supports(SpecType type) {
        return type instanceof DateSpecType;
    }

    @Override
    public TypeReference create(String specPropertyName, SpecType specType, MappingContext ctx) {
        return TypeReference.builder()
                .packageName("java.time")
                .simpleName("LocalDate")
                .build();
    }
}
