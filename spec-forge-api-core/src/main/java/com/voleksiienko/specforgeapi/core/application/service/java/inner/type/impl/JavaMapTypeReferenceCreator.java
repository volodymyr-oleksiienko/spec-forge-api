package com.voleksiienko.specforgeapi.core.application.service.java.inner.type.impl;

import com.voleksiienko.specforgeapi.core.application.annotation.Component;
import com.voleksiienko.specforgeapi.core.application.service.java.inner.type.JavaMappingContext;
import com.voleksiienko.specforgeapi.core.application.service.java.inner.type.JavaTypeReferenceCreator;
import com.voleksiienko.specforgeapi.core.application.service.java.inner.type.JavaTypeReferenceCreatorFacade;
import com.voleksiienko.specforgeapi.core.domain.model.java.TypeReference;
import com.voleksiienko.specforgeapi.core.domain.model.spec.type.MapSpecType;
import com.voleksiienko.specforgeapi.core.domain.model.spec.type.SpecType;
import java.util.List;

@Component
public class JavaMapTypeReferenceCreator implements JavaTypeReferenceCreator {

    private final JavaTypeReferenceCreatorFacade javaTypeReferenceCreatorFacade;

    public JavaMapTypeReferenceCreator(JavaTypeReferenceCreatorFacade javaTypeReferenceCreatorFacade) {
        this.javaTypeReferenceCreatorFacade = javaTypeReferenceCreatorFacade;
    }

    @Override
    public boolean supports(SpecType type) {
        return type instanceof MapSpecType;
    }

    @Override
    public TypeReference create(String specPropertyName, SpecType specType, JavaMappingContext ctx) {
        MapSpecType mapType = (MapSpecType) specType;
        TypeReference keyRef = javaTypeReferenceCreatorFacade.create(
                specPropertyName, mapType.getKeyType(), new JavaMappingContext(ctx, true, true));
        TypeReference valRef = javaTypeReferenceCreatorFacade.create(
                specPropertyName, mapType.getValueType(), new JavaMappingContext(ctx, true, true));
        return TypeReference.builder()
                .packageName("java.util")
                .simpleName("Map")
                .genericArguments(List.of(keyRef, valRef))
                .build();
    }
}
