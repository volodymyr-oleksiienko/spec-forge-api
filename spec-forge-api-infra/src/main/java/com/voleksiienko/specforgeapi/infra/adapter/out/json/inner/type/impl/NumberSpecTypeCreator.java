package com.voleksiienko.specforgeapi.infra.adapter.out.json.inner.type.impl;

import com.voleksiienko.specforgeapi.core.domain.model.spec.type.DoubleSpecType;
import com.voleksiienko.specforgeapi.core.domain.model.spec.type.SpecType;
import com.voleksiienko.specforgeapi.infra.adapter.out.json.inner.ParsingContext;
import com.voleksiienko.specforgeapi.infra.adapter.out.json.inner.type.SpecTypeCreator;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;

@Component
public class NumberSpecTypeCreator implements SpecTypeCreator {

    @Override
    public boolean supports(JsonNode node, String type) {
        return "number".equals(type);
    }

    @Override
    public SpecType createType(JsonNode node, ParsingContext parsingContext) {
        DoubleSpecType.Builder builder = DoubleSpecType.builder();
        if (node.has("exclusiveMinimum")) {
            builder.minimum(Math.nextUp(node.get("exclusiveMinimum").asDouble()));
        } else if (node.has("minimum")) {
            builder.minimum(node.get("minimum").asDouble());
        }
        if (node.has("exclusiveMaximum")) {
            builder.maximum(Math.nextDown(node.get("exclusiveMaximum").asDouble()));
        } else if (node.has("maximum")) {
            builder.maximum(node.get("maximum").asDouble());
        }
        return builder.build();
    }
}
