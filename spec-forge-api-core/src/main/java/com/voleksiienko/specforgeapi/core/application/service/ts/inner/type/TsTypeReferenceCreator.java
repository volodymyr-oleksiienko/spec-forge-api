package com.voleksiienko.specforgeapi.core.application.service.ts.inner.type;

import com.voleksiienko.specforgeapi.core.domain.model.spec.type.SpecType;
import com.voleksiienko.specforgeapi.core.domain.model.ts.TsTypeReference;

public interface TsTypeReferenceCreator {

    boolean supports(SpecType type);

    TsTypeReference create(String propName, SpecType type, TsMappingContext ctx);
}
