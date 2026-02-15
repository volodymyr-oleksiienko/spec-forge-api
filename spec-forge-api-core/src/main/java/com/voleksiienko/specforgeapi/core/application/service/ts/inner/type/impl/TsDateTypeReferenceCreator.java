package com.voleksiienko.specforgeapi.core.application.service.ts.inner.type.impl;

import com.voleksiienko.specforgeapi.core.application.annotation.Component;
import com.voleksiienko.specforgeapi.core.application.service.ts.inner.type.TsMappingContext;
import com.voleksiienko.specforgeapi.core.application.service.ts.inner.type.TsTypeReferenceCreator;
import com.voleksiienko.specforgeapi.core.domain.model.spec.type.DateSpecType;
import com.voleksiienko.specforgeapi.core.domain.model.spec.type.DateTimeSpecType;
import com.voleksiienko.specforgeapi.core.domain.model.spec.type.SpecType;
import com.voleksiienko.specforgeapi.core.domain.model.spec.type.TimeSpecType;
import com.voleksiienko.specforgeapi.core.domain.model.ts.TsTypeReference;

@Component
public class TsDateTypeReferenceCreator implements TsTypeReferenceCreator {

    @Override
    public boolean supports(SpecType type) {
        return type instanceof DateSpecType || type instanceof DateTimeSpecType || type instanceof TimeSpecType;
    }

    @Override
    public TsTypeReference create(String propName, SpecType type, TsMappingContext ctx) {
        return TsTypeReference.builder().typeName("string").build();
    }
}
