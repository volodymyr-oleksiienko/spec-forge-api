package com.voleksiienko.specforgeapi.infra.adapter.out.json.inner.type.impl;

import static com.voleksiienko.specforgeapi.core.domain.model.error.JsonMappingErrorCode.JSON_SCHEMA_MAP_KEY_DEFAULTED_TO_STRING;

import com.voleksiienko.specforgeapi.core.domain.model.spec.SpecProperty;
import com.voleksiienko.specforgeapi.core.domain.model.spec.type.MapSpecType;
import com.voleksiienko.specforgeapi.core.domain.model.spec.type.ObjectSpecType;
import com.voleksiienko.specforgeapi.core.domain.model.spec.type.SpecType;
import com.voleksiienko.specforgeapi.core.domain.model.spec.type.StringSpecType;
import com.voleksiienko.specforgeapi.infra.adapter.out.json.inner.ParsingContext;
import com.voleksiienko.specforgeapi.infra.adapter.out.json.inner.type.SpecTypeCreator;
import com.voleksiienko.specforgeapi.infra.adapter.out.json.inner.type.SpecTypeCreatorFacade;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;

@Component
@RequiredArgsConstructor
public class ObjectSpecTypeCreator implements SpecTypeCreator {

    private final SpecTypeCreatorFacade specTypeCreatorFacade;

    @Override
    public boolean supports(JsonNode node, String type) {
        return "object".equals(type);
    }

    @Override
    public SpecType createType(
            JsonNode node,
            ParsingContext parsingContext,
            List<String> strings,
            BiFunction<JsonNode, ParsingContext, List<SpecProperty>> propertyCreator) {
        JsonNode additionalProps = node.get("additionalProperties");
        return Objects.nonNull(additionalProps) && additionalProps.isObject()
                ? buildMapNodeType(additionalProps, parsingContext, propertyCreator)
                : ObjectSpecType.builder()
                        .children(propertyCreator.apply(node, parsingContext))
                        .build();
    }

    private SpecType buildMapNodeType(
            JsonNode additionalProps,
            ParsingContext parsingContext,
            BiFunction<JsonNode, ParsingContext, List<SpecProperty>> propertyCreator) {
        parsingContext.addWarning("Map [%s] key type is treated as 'string'", JSON_SCHEMA_MAP_KEY_DEFAULTED_TO_STRING);
        return MapSpecType.builder()
                .keyType(StringSpecType.builder().build())
                .valueType(specTypeCreatorFacade.create(additionalProps, parsingContext, propertyCreator))
                .build();
    }
}
