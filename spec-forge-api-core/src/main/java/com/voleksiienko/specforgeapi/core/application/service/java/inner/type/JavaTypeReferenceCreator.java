package com.voleksiienko.specforgeapi.core.application.service.java.inner.type;

import com.voleksiienko.specforgeapi.core.domain.model.java.TypeReference;
import com.voleksiienko.specforgeapi.core.domain.model.spec.type.SpecType;

public interface JavaTypeReferenceCreator {

    boolean supports(SpecType type);

    TypeReference create(String specPropertyName, SpecType specType, JavaMappingContext ctx);
}
