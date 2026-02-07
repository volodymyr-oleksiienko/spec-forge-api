package com.voleksiienko.specforgeapi.infra.adapter.out.json.inner.type.impl;

import com.voleksiienko.specforgeapi.core.domain.model.spec.SpecProperty;
import com.voleksiienko.specforgeapi.core.domain.model.spec.type.BooleanSpecType;
import com.voleksiienko.specforgeapi.core.domain.model.spec.type.SpecType;
import com.voleksiienko.specforgeapi.infra.adapter.out.json.inner.ParsingContext;
import com.voleksiienko.specforgeapi.infra.adapter.out.json.inner.type.SpecTypeCreator;
import java.util.List;
import java.util.function.BiFunction;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;

@Component
public class BooleanSpecTypeCreator implements SpecTypeCreator {

    @Override
    public boolean supports(JsonNode node, String type) {
        return "boolean".equals(type);
    }

    @Override
    public SpecType createType(
            JsonNode node,
            ParsingContext parsingContext,
            List<String> examples,
            BiFunction<JsonNode, ParsingContext, List<SpecProperty>> propertyCreator) {
        return BooleanSpecType.builder().examples(examples).build();
    }
}
