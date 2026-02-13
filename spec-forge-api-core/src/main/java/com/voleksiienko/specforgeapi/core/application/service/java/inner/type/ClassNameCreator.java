package com.voleksiienko.specforgeapi.core.application.service.java.inner.type;

import com.voleksiienko.specforgeapi.core.application.annotation.Component;
import com.voleksiienko.specforgeapi.core.application.port.out.util.StringInflectorPort;

@Component
public class ClassNameCreator {

    private final StringInflectorPort stringInflector;

    public ClassNameCreator(StringInflectorPort stringInflector) {
        this.stringInflector = stringInflector;
    }

    public String create(String specPropertyName, MappingContext ctx) {
        String className = stringInflector.capitalize(
                ctx.isPluralContext() ? stringInflector.singularize(specPropertyName) : specPropertyName);
        if (ctx.javaTypes().stream().anyMatch(javaType -> javaType.getName().equals(className))) {
            return ctx.rootClassName() + className;
        }
        return className;
    }
}
