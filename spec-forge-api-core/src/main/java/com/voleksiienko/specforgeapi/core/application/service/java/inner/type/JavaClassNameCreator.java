package com.voleksiienko.specforgeapi.core.application.service.java.inner.type;

import com.voleksiienko.specforgeapi.core.application.annotation.Component;
import com.voleksiienko.specforgeapi.core.application.port.out.util.StringInflectorPort;

@Component
public class JavaClassNameCreator {

    private final StringInflectorPort stringInflector;

    public JavaClassNameCreator(StringInflectorPort stringInflector) {
        this.stringInflector = stringInflector;
    }

    public String create(String specPropertyName, JavaMappingContext ctx) {
        String className = stringInflector.capitalize(
                ctx.isPluralContext() ? stringInflector.singularize(specPropertyName) : specPropertyName);
        if (ctx.javaTypes().stream().anyMatch(javaType -> javaType.getName().equals(className))) {
            return ctx.rootClassName() + className;
        }
        return className;
    }
}
