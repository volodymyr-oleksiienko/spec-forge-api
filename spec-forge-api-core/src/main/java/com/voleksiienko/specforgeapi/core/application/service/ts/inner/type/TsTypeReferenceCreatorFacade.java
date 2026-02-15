package com.voleksiienko.specforgeapi.core.application.service.ts.inner.type;

import com.voleksiienko.specforgeapi.core.domain.model.spec.type.SpecType;
import com.voleksiienko.specforgeapi.core.domain.model.ts.TsTypeReference;
import java.util.List;

public class TsTypeReferenceCreatorFacade {

    private final List<TsTypeReferenceCreator> creators;

    public TsTypeReferenceCreatorFacade(List<TsTypeReferenceCreator> creators) {
        this.creators = creators;
    }

    public TsTypeReference create(String propName, SpecType specType, TsMappingContext ctx) {
        return creators.stream()
                .filter(c -> c.supports(specType))
                .findFirst()
                .map(c -> c.create(propName, specType, ctx))
                .orElseThrow(() -> new IllegalArgumentException("No TS creator found for spec type: "
                        + specType.getClass().getSimpleName()));
    }
}
