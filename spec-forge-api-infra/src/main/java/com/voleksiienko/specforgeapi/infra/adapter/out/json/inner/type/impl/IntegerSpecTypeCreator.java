package com.voleksiienko.specforgeapi.infra.adapter.out.json.inner.type.impl;

import com.voleksiienko.specforgeapi.core.domain.model.spec.type.IntegerSpecType;
import com.voleksiienko.specforgeapi.core.domain.model.spec.type.SpecType;
import com.voleksiienko.specforgeapi.infra.adapter.out.json.inner.ParsingContext;
import com.voleksiienko.specforgeapi.infra.adapter.out.json.inner.type.SpecTypeCreator;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;

@Component
public class IntegerSpecTypeCreator implements SpecTypeCreator {

    @Override
    public boolean supports(JsonNode node, String type) {
        return "integer".equals(type);
    }

    @Override
    public SpecType createType(JsonNode node, ParsingContext parsingContext) {
        IntegerSpecType.Builder builder = IntegerSpecType.builder();
        if (node.has("exclusiveMinimum")) {
            builder.minimum(node.get("exclusiveMinimum").asLong() + 1);
        } else if (node.has("minimum")) {
            builder.minimum(node.get("minimum").asLong());
        }
        if (node.has("exclusiveMaximum")) {
            builder.maximum(node.get("exclusiveMaximum").asLong() - 1);
        } else if (node.has("maximum")) {
            builder.maximum(node.get("maximum").asLong());
        }
        return builder.build();
    }
}
