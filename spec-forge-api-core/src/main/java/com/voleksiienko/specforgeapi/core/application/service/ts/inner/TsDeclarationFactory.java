package com.voleksiienko.specforgeapi.core.application.service.ts.inner;

import com.voleksiienko.specforgeapi.core.application.annotation.Component;
import com.voleksiienko.specforgeapi.core.application.service.ts.inner.type.TsMappingContext;
import com.voleksiienko.specforgeapi.core.application.service.ts.inner.type.TsTypeReferenceCreatorFacade;
import com.voleksiienko.specforgeapi.core.domain.model.config.TypeScriptConfig.Structure.DeclarationStyle;
import com.voleksiienko.specforgeapi.core.domain.model.spec.SpecProperty;
import com.voleksiienko.specforgeapi.core.domain.model.ts.*;
import java.util.List;

@Component
public class TsDeclarationFactory {

    private final TsTypeReferenceCreatorFacade typeFacade;
    private final TsFieldSorter tsFieldSorter;
    private final TypeScriptFieldNameSanitizer typeScriptFieldNameSanitizer;

    public TsDeclarationFactory(
            TsTypeReferenceCreatorFacade typeFacade,
            TsFieldSorter tsFieldSorter,
            TypeScriptFieldNameSanitizer typeScriptFieldNameSanitizer) {
        this.typeFacade = typeFacade;
        this.tsFieldSorter = tsFieldSorter;
        this.typeScriptFieldNameSanitizer = typeScriptFieldNameSanitizer;
    }

    public TsDeclaration createObject(String name, List<SpecProperty> properties, TsMappingContext ctx) {
        List<TsField> fields = properties.stream().map(p -> mapField(p, ctx)).toList();
        List<TsField> sortedFields = tsFieldSorter.sort(fields, ctx.config());
        if (ctx.config().structure().style() == DeclarationStyle.INTERFACE) {
            return TsInterface.builder().name(name).fields(sortedFields).build();
        } else {
            return TsTypeAlias.builder().name(name).fields(sortedFields).build();
        }
    }

    private TsField mapField(SpecProperty property, TsMappingContext ctx) {
        TsTypeReference typeRef = typeFacade.create(property.getName(), property.getType(), ctx);
        String fieldName = typeScriptFieldNameSanitizer.sanitize(property.getName());
        return TsField.builder()
                .name(
                        !property.getName().equals(fieldName)
                                ? "\"%s\"".formatted(property.getName())
                                : property.getName())
                .type(typeRef)
                .optional(!property.isRequired())
                .build();
    }
}
