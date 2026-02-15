package com.voleksiienko.specforgeapi.core.application.service.ts.inner.type.impl;

import com.voleksiienko.specforgeapi.core.application.annotation.Component;
import com.voleksiienko.specforgeapi.core.application.service.ts.inner.type.TsMappingContext;
import com.voleksiienko.specforgeapi.core.application.service.ts.inner.type.TsTypeReferenceCreator;
import com.voleksiienko.specforgeapi.core.application.service.ts.inner.type.TsTypeReferenceCreatorFacade;
import com.voleksiienko.specforgeapi.core.domain.model.spec.type.MapSpecType;
import com.voleksiienko.specforgeapi.core.domain.model.spec.type.SpecType;
import com.voleksiienko.specforgeapi.core.domain.model.ts.TsTypeReference;
import java.util.List;

@Component
public class TsMapTypeReferenceCreator implements TsTypeReferenceCreator {

    private final TsTypeReferenceCreatorFacade facade;

    public TsMapTypeReferenceCreator(TsTypeReferenceCreatorFacade facade) {
        this.facade = facade;
    }

    @Override
    public boolean supports(SpecType type) {
        return type instanceof MapSpecType;
    }

    @Override
    public TsTypeReference create(String propName, SpecType type, TsMappingContext ctx) {
        MapSpecType mapType = (MapSpecType) type;
        TsTypeReference keyRef = facade.create(propName + "Key", mapType.getKeyType(), new TsMappingContext(ctx, true));
        TsTypeReference valueRef =
                facade.create(propName + "Value", mapType.getValueType(), new TsMappingContext(ctx, true));
        return TsTypeReference.builder()
                .typeName("Record")
                .genericArguments(List.of(keyRef, valueRef))
                .build();
    }
}
