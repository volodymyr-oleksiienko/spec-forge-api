package com.voleksiienko.specforgeapi.core.application.service.java.inner.type.impl;

import com.voleksiienko.specforgeapi.core.application.annotation.Component;
import com.voleksiienko.specforgeapi.core.application.service.java.inner.type.JavaMappingContext;
import com.voleksiienko.specforgeapi.core.application.service.java.inner.type.JavaTypeReferenceCreator;
import com.voleksiienko.specforgeapi.core.application.service.java.inner.type.JavaTypeReferenceCreatorFacade;
import com.voleksiienko.specforgeapi.core.domain.model.java.TypeReference;
import com.voleksiienko.specforgeapi.core.domain.model.spec.type.ListSpecType;
import com.voleksiienko.specforgeapi.core.domain.model.spec.type.SpecType;
import java.util.List;

@Component
public class JavaListTypeReferenceCreator implements JavaTypeReferenceCreator {

    private final JavaTypeReferenceCreatorFacade facade;

    public JavaListTypeReferenceCreator(JavaTypeReferenceCreatorFacade facade) {
        this.facade = facade;
    }

    @Override
    public boolean supports(SpecType type) {
        return type instanceof ListSpecType;
    }

    @Override
    public TypeReference create(String specPropertyName, SpecType specType, JavaMappingContext ctx) {
        TypeReference valueRef = facade.create(
                specPropertyName, ((ListSpecType) specType).getValueType(), new JavaMappingContext(ctx, true, true));
        return TypeReference.builder()
                .packageName("java.util")
                .simpleName("List")
                .genericArguments(List.of(valueRef))
                .build();
    }
}
