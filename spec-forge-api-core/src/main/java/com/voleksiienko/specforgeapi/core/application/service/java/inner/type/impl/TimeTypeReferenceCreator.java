package com.voleksiienko.specforgeapi.core.application.service.java.inner.type.impl;

import com.voleksiienko.specforgeapi.core.application.annotation.Component;
import com.voleksiienko.specforgeapi.core.application.service.java.inner.type.MappingContext;
import com.voleksiienko.specforgeapi.core.application.service.java.inner.type.TypeReferenceCreator;
import com.voleksiienko.specforgeapi.core.domain.model.java.TypeReference;
import com.voleksiienko.specforgeapi.core.domain.model.spec.type.SpecType;
import com.voleksiienko.specforgeapi.core.domain.model.spec.type.TimeSpecType;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Component
public class TimeTypeReferenceCreator implements TypeReferenceCreator {

    @Override
    public boolean supports(SpecType type) {
        return type instanceof TimeSpecType;
    }

    @Override
    public TypeReference create(String specPropertyName, SpecType specType, MappingContext ctx) {
        if (isLocalCompatible(((TimeSpecType) specType).getFormat())) {
            return TypeReference.builder()
                    .packageName("java.time")
                    .simpleName("LocalTime")
                    .build();
        } else {
            return TypeReference.builder()
                    .packageName("java.time")
                    .simpleName("OffsetTime")
                    .build();
        }
    }

    private boolean isLocalCompatible(String pattern) {
        try {
            LocalTime.now().format(DateTimeFormatter.ofPattern(pattern));
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
