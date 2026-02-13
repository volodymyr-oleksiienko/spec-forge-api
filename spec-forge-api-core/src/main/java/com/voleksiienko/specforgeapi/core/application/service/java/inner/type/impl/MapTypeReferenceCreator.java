package com.voleksiienko.specforgeapi.core.application.service.java.inner.type.impl;

import com.voleksiienko.specforgeapi.core.application.annotation.Component;
import com.voleksiienko.specforgeapi.core.application.service.java.inner.type.MappingContext;
import com.voleksiienko.specforgeapi.core.application.service.java.inner.type.TypeReferenceCreator;
import com.voleksiienko.specforgeapi.core.application.service.java.inner.type.TypeReferenceCreatorFacade;
import com.voleksiienko.specforgeapi.core.domain.model.java.TypeReference;
import com.voleksiienko.specforgeapi.core.domain.model.spec.type.MapSpecType;
import com.voleksiienko.specforgeapi.core.domain.model.spec.type.SpecType;
import java.util.List;

@Component
public class MapTypeReferenceCreator implements TypeReferenceCreator {

    private final TypeReferenceCreatorFacade typeReferenceCreatorFacade;

    public MapTypeReferenceCreator(TypeReferenceCreatorFacade typeReferenceCreatorFacade) {
        this.typeReferenceCreatorFacade = typeReferenceCreatorFacade;
    }

    @Override
    public boolean supports(SpecType type) {
        return type instanceof MapSpecType;
    }

    @Override
    public TypeReference create(String specPropertyName, SpecType specType, MappingContext ctx) {
        MapSpecType mapType = (MapSpecType) specType;
        TypeReference keyRef = typeReferenceCreatorFacade.create(
                specPropertyName, mapType.getKeyType(), new MappingContext(ctx, true, true));
        TypeReference valRef = typeReferenceCreatorFacade.create(
                specPropertyName, mapType.getValueType(), new MappingContext(ctx, true, true));
        return TypeReference.builder()
                .packageName("java.util")
                .simpleName("Map")
                .genericArguments(List.of(keyRef, valRef))
                .build();
    }
}
