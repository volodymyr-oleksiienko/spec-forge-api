package com.voleksiienko.specforgeapi.core.application.service.ts.inner.type.impl;

import com.voleksiienko.specforgeapi.core.application.annotation.Component;
import com.voleksiienko.specforgeapi.core.application.service.ts.inner.type.TsMappingContext;
import com.voleksiienko.specforgeapi.core.application.service.ts.inner.type.TsTypeReferenceCreator;
import com.voleksiienko.specforgeapi.core.domain.model.spec.type.*;
import com.voleksiienko.specforgeapi.core.domain.model.ts.TsTypeReference;

@Component
public class TsPrimitiveTypeReferenceCreator implements TsTypeReferenceCreator {

    @Override
    public boolean supports(SpecType type) {
        return type instanceof StringSpecType
                || type instanceof IntegerSpecType
                || type instanceof DoubleSpecType
                || type instanceof DecimalSpecType
                || type instanceof BooleanSpecType;
    }

    @Override
    public TsTypeReference create(String propName, SpecType type, TsMappingContext ctx) {
        String typeName =
                switch (type) {
                    case IntegerSpecType _, DoubleSpecType _, DecimalSpecType _ -> "number";
                    case BooleanSpecType _ -> "boolean";
                    default -> "string";
                };
        return TsTypeReference.builder().typeName(typeName).build();
    }
}
