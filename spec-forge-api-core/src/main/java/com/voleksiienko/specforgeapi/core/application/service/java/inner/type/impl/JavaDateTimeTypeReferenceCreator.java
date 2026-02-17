package com.voleksiienko.specforgeapi.core.application.service.java.inner.type.impl;

import com.voleksiienko.specforgeapi.core.application.annotation.Component;
import com.voleksiienko.specforgeapi.core.application.service.java.inner.type.JavaMappingContext;
import com.voleksiienko.specforgeapi.core.application.service.java.inner.type.JavaTypeReferenceCreator;
import com.voleksiienko.specforgeapi.core.domain.model.java.TypeReference;
import com.voleksiienko.specforgeapi.core.domain.model.spec.type.DateTimeSpecType;
import com.voleksiienko.specforgeapi.core.domain.model.spec.type.SpecType;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class JavaDateTimeTypeReferenceCreator implements JavaTypeReferenceCreator {

    @Override
    public boolean supports(SpecType type) {
        return type instanceof DateTimeSpecType;
    }

    @Override
    public TypeReference create(String specPropertyName, SpecType specType, JavaMappingContext ctx) {
        if (isLocalCompatible(((DateTimeSpecType) specType).getFormat())) {
            return TypeReference.builder()
                    .packageName("java.time")
                    .simpleName("LocalDateTime")
                    .build();
        } else {
            return TypeReference.builder()
                    .packageName("java.time")
                    .simpleName("OffsetDateTime")
                    .build();
        }
    }

    private boolean isLocalCompatible(String pattern) {
        try {
            LocalDateTime.now().format(DateTimeFormatter.ofPattern(pattern));
            return true;
        } catch (Exception _) {
            return false;
        }
    }
}
