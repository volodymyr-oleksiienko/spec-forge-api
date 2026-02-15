package com.voleksiienko.specforgeapi.core.application.service.ts.inner.type.impl;

import com.voleksiienko.specforgeapi.core.application.annotation.Component;
import com.voleksiienko.specforgeapi.core.application.service.ts.inner.type.TsMappingContext;
import com.voleksiienko.specforgeapi.core.application.service.ts.inner.type.TsTypeReferenceCreator;
import com.voleksiienko.specforgeapi.core.application.service.ts.inner.type.TsTypeReferenceCreatorFacade;
import com.voleksiienko.specforgeapi.core.domain.model.spec.type.ListSpecType;
import com.voleksiienko.specforgeapi.core.domain.model.spec.type.SpecType;
import com.voleksiienko.specforgeapi.core.domain.model.ts.TsTypeReference;
import java.util.List;

@Component
public class TsListTypeReferenceCreator implements TsTypeReferenceCreator {

    private final TsTypeReferenceCreatorFacade facade;

    public TsListTypeReferenceCreator(TsTypeReferenceCreatorFacade facade) {
        this.facade = facade;
    }

    @Override
    public boolean supports(SpecType type) {
        return type instanceof ListSpecType;
    }

    @Override
    public TsTypeReference create(String propName, SpecType type, TsMappingContext ctx) {
        TsTypeReference itemType =
                facade.create(propName, ((ListSpecType) type).getValueType(), new TsMappingContext(ctx, true));
        return TsTypeReference.builder()
                .typeName("Array")
                .genericArguments(List.of(itemType))
                .build();
    }
}
