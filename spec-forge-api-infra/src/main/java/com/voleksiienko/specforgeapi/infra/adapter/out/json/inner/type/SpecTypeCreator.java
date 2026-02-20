package com.voleksiienko.specforgeapi.infra.adapter.out.json.inner.type;

import com.voleksiienko.specforgeapi.core.domain.model.spec.SpecProperty;
import com.voleksiienko.specforgeapi.core.domain.model.spec.type.SpecType;
import com.voleksiienko.specforgeapi.infra.adapter.out.json.inner.ParsingContext;
import java.util.List;
import java.util.function.BiFunction;
import tools.jackson.databind.JsonNode;

public interface SpecTypeCreator {

    boolean supports(JsonNode node, String type);

    SpecType createType(
            JsonNode node,
            ParsingContext parsingContext,
            List<String> examples,
            BiFunction<JsonNode, ParsingContext, List<SpecProperty>> propertyCreator);
}
