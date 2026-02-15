package com.voleksiienko.specforgeapi.core.application.service.ts.inner.type.impl;

import com.voleksiienko.specforgeapi.core.application.annotation.Component;
import com.voleksiienko.specforgeapi.core.application.port.out.util.StringInflectorPort;
import com.voleksiienko.specforgeapi.core.application.service.ts.inner.type.TsMappingContext;
import com.voleksiienko.specforgeapi.core.application.service.ts.inner.type.TsTypeReferenceCreator;
import com.voleksiienko.specforgeapi.core.domain.model.config.TypeScriptConfig.Enums.EnumStyle;
import com.voleksiienko.specforgeapi.core.domain.model.spec.type.EnumSpecType;
import com.voleksiienko.specforgeapi.core.domain.model.spec.type.SpecType;
import com.voleksiienko.specforgeapi.core.domain.model.ts.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class TsEnumTypeReferenceCreator implements TsTypeReferenceCreator {

    private final StringInflectorPort inflector;

    public TsEnumTypeReferenceCreator(StringInflectorPort inflector) {
        this.inflector = inflector;
    }

    @Override
    public boolean supports(SpecType type) {
        return type instanceof EnumSpecType;
    }

    @Override
    public TsTypeReference create(String propName, SpecType type, TsMappingContext ctx) {
        EnumSpecType specEnum = (EnumSpecType) type;
        String enumName = inflector.capitalize(propName);
        List<String> values = new ArrayList<>(specEnum.getValues());
        TsDeclaration declaration;
        if (ctx.config().enums().style() == EnumStyle.UNION_STRING) {
            declaration = TsUnionType.builder().name(enumName).values(values).build();
        } else {
            List<TsEnumConstant> constants = values.stream()
                    .map(value ->
                            TsEnumConstant.builder().key(value).value(value).build())
                    .toList();
            declaration = TsEnum.builder().name(enumName).constants(constants).build();
        }
        ctx.declarations().add(declaration);
        return TsTypeReference.builder().typeName(enumName).build();
    }
}
