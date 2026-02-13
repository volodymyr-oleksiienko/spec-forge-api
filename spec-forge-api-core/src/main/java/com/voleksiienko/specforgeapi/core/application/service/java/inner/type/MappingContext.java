package com.voleksiienko.specforgeapi.core.application.service.java.inner.type;

import com.voleksiienko.specforgeapi.core.domain.model.config.JavaConfig;
import com.voleksiienko.specforgeapi.core.domain.model.java.JavaType;
import java.util.List;

public record MappingContext(
        String rootClassName,
        List<JavaType> javaTypes,
        JavaConfig config,
        boolean usePrimitiveWrappers,
        boolean isPluralContext) {

    public MappingContext(MappingContext ctx, boolean usePrimitiveWrappers, boolean isPluralContext) {
        this(ctx.rootClassName(), ctx.javaTypes(), ctx.config(), usePrimitiveWrappers, isPluralContext);
    }
}
