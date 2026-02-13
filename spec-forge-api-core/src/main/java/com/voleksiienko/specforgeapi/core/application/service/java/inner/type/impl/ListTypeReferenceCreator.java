package com.voleksiienko.specforgeapi.core.application.service.java.inner.type.impl;

import com.voleksiienko.specforgeapi.core.application.annotation.Component;
import com.voleksiienko.specforgeapi.core.application.service.java.inner.type.MappingContext;
import com.voleksiienko.specforgeapi.core.application.service.java.inner.type.TypeReferenceCreator;
import com.voleksiienko.specforgeapi.core.application.service.java.inner.type.TypeReferenceCreatorFacade;
import com.voleksiienko.specforgeapi.core.domain.model.java.TypeReference;
import com.voleksiienko.specforgeapi.core.domain.model.spec.type.ListSpecType;
import com.voleksiienko.specforgeapi.core.domain.model.spec.type.SpecType;
import java.util.List;

@Component
public class ListTypeReferenceCreator implements TypeReferenceCreator {

    private final TypeReferenceCreatorFacade facade;

    public ListTypeReferenceCreator(TypeReferenceCreatorFacade facade) {
        this.facade = facade;
    }

    @Override
    public boolean supports(SpecType type) {
        return type instanceof ListSpecType;
    }

    @Override
    public TypeReference create(String specPropertyName, SpecType specType, MappingContext ctx) {
        TypeReference valueRef = facade.create(
                specPropertyName, ((ListSpecType) specType).getValueType(), new MappingContext(ctx, true, true));
        return TypeReference.builder()
                .packageName("java.util")
                .simpleName("List")
                .genericArguments(List.of(valueRef))
                .build();
    }
}
