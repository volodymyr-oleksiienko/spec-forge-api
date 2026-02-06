package com.voleksiienko.specforgeapi.infra.adapter.out.json.inner.type.impl;

import static com.voleksiienko.specforgeapi.core.domain.model.error.JsonMappingErrorCode.JSON_SCHEMA_LIST_VALUE_DEFAULTED_TO_STRING;

import com.voleksiienko.specforgeapi.core.domain.model.spec.type.ListSpecType;
import com.voleksiienko.specforgeapi.core.domain.model.spec.type.SpecType;
import com.voleksiienko.specforgeapi.core.domain.model.spec.type.StringSpecType;
import com.voleksiienko.specforgeapi.infra.adapter.out.json.inner.ParsingContext;
import com.voleksiienko.specforgeapi.infra.adapter.out.json.inner.type.SpecTypeCreator;
import com.voleksiienko.specforgeapi.infra.adapter.out.json.inner.type.SpecTypeCreatorFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;

@Component
@RequiredArgsConstructor
public class ArraySpecTypeCreator implements SpecTypeCreator {

    private final SpecTypeCreatorFacade specTypeCreatorFacade;

    @Override
    public boolean supports(JsonNode node, String type) {
        return "array".equals(type);
    }

    @Override
    public SpecType createType(JsonNode node, ParsingContext parsingContext) {
        return ListSpecType.builder()
                .valueType(getValueType(node, parsingContext))
                .maxItems(node.has("maxItems") ? node.get("maxItems").asInt() : null)
                .minItems(node.has("minItems") ? node.get("minItems").asInt() : null)
                .build();
    }

    private SpecType getValueType(JsonNode node, ParsingContext parsingContext) {
        if (node.has("items")) {
            return specTypeCreatorFacade.create(node.get("items"), parsingContext);
        }
        parsingContext.addWarning(
                "Array 'items' definition is missing, defaulting elements to 'string'",
                JSON_SCHEMA_LIST_VALUE_DEFAULTED_TO_STRING);
        return StringSpecType.builder().build();
    }
}
