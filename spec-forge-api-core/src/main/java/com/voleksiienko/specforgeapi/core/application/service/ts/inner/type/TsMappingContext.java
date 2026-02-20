package com.voleksiienko.specforgeapi.core.application.service.ts.inner.type;

import com.voleksiienko.specforgeapi.core.domain.model.config.TypeScriptConfig;
import com.voleksiienko.specforgeapi.core.domain.model.ts.TsDeclaration;
import java.util.List;

public record TsMappingContext(
        String rootName, List<TsDeclaration> declarations, TypeScriptConfig config, boolean isPluralContext) {

    public TsMappingContext(TsMappingContext context, boolean isPluralContext) {
        this(context.rootName, context.declarations, context.config, isPluralContext);
    }
}
