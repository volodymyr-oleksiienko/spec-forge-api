package com.voleksiienko.specforgeapi.core.application.service.ts.inner.type.impl;

import com.voleksiienko.specforgeapi.core.application.annotation.Component;
import com.voleksiienko.specforgeapi.core.application.port.out.util.StringInflectorPort;
import com.voleksiienko.specforgeapi.core.application.service.ts.inner.TsDeclarationFactory;
import com.voleksiienko.specforgeapi.core.application.service.ts.inner.type.TsMappingContext;
import com.voleksiienko.specforgeapi.core.application.service.ts.inner.type.TsTypeReferenceCreator;
import com.voleksiienko.specforgeapi.core.domain.model.spec.type.ObjectSpecType;
import com.voleksiienko.specforgeapi.core.domain.model.spec.type.SpecType;
import com.voleksiienko.specforgeapi.core.domain.model.ts.TsDeclaration;
import com.voleksiienko.specforgeapi.core.domain.model.ts.TsTypeReference;

@Component
public class TsObjectTypeReferenceCreator implements TsTypeReferenceCreator {

    private final TsDeclarationFactory declarationFactory;
    private final StringInflectorPort stringInflector;

    public TsObjectTypeReferenceCreator(TsDeclarationFactory declarationFactory, StringInflectorPort stringInflector) {
        this.declarationFactory = declarationFactory;
        this.stringInflector = stringInflector;
    }

    @Override
    public boolean supports(SpecType type) {
        return type instanceof ObjectSpecType;
    }

    @Override
    public TsTypeReference create(String propName, SpecType type, TsMappingContext ctx) {
        ObjectSpecType objType = (ObjectSpecType) type;
        String className =
                stringInflector.capitalize(ctx.isPluralContext() ? stringInflector.singularize(propName) : propName);
        TsDeclaration nested =
                declarationFactory.createObject(className, objType.getChildren(), new TsMappingContext(ctx, false));
        ctx.declarations().add(nested);
        return TsTypeReference.builder().typeName(className).build();
    }
}
