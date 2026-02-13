package com.voleksiienko.specforgeapi.infra.adapter.out.util;

import com.voleksiienko.specforgeapi.core.application.port.out.util.StringInflectorPort;
import org.modeshape.common.text.Inflector;
import org.springframework.stereotype.Component;

@Component
public class StringInflectorAdapter implements StringInflectorPort {

    private static final Inflector INFLECTOR = Inflector.getInstance();

    @Override
    public String singularize(String word) {
        return INFLECTOR.singularize(word);
    }

    @Override
    public String capitalize(String word) {
        return INFLECTOR.capitalize(word);
    }
}
