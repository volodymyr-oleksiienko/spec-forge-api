package com.voleksiienko.specforgeapi.infra.adapter.out.json.inner.type;

import com.voleksiienko.specforgeapi.core.domain.model.spec.type.SpecType;
import com.voleksiienko.specforgeapi.infra.adapter.out.json.inner.ParsingContext;
import tools.jackson.databind.JsonNode;

public interface SpecTypeCreator {

    boolean supports(JsonNode node, String type);

    SpecType createType(JsonNode node, ParsingContext parsingContext);
}
