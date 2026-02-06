package com.voleksiienko.specforgeapi.infra.adapter.out.json.inner;

import com.voleksiienko.specforgeapi.core.domain.model.spec.SpecProperty;
import com.voleksiienko.specforgeapi.core.domain.model.spec.type.SpecType;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;

@Component
public class SpecPropertyMapper {

    public SpecProperty mapToNode(
            JsonSchemaParser.ChildDefinition definition, SpecType specType, List<SpecProperty> children) {
        JsonNode node = definition.node();
        return SpecProperty.builder()
                .name(definition.name())
                .required(definition.isRequired() && !isNullable(node))
                .description(node.path("description").asString(null))
                .deprecated(node.path("deprecated").asBoolean(false))
                .examples(mapToExamples(definition.node()))
                .type(specType)
                .children(children)
                .build();
    }

    private boolean isNullable(JsonNode node) {
        JsonNode typeNode = node.path("type");
        if (typeNode.isArray()) {
            for (JsonNode t : typeNode) {
                if ("null".equalsIgnoreCase(t.asString(null))) {
                    return true;
                }
            }
        }
        return false;
    }

    private List<String> mapToExamples(JsonNode node) {
        List<String> examples = new ArrayList<>();
        if (node.has("examples")) {
            node.get("examples").forEach(ex -> examples.add(ex.asString(null)));
        } else if (node.has("example")) {
            examples.add(node.get("example").asString());
        }
        return examples;
    }
}
