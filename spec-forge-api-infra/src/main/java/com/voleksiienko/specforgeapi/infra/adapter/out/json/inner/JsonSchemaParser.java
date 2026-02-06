package com.voleksiienko.specforgeapi.infra.adapter.out.json.inner;

import static com.voleksiienko.specforgeapi.core.domain.model.error.JsonMappingErrorCode.JSON_SCHEMA_COMPOSITION_PROPERTY_DUPLICATE;
import static com.voleksiienko.specforgeapi.core.domain.model.error.JsonMappingErrorCode.JSON_SCHEMA_COMPOSITION_PROPERTY_MERGED;

import com.voleksiienko.specforgeapi.core.domain.model.spec.SpecModel;
import java.util.*;
import java.util.stream.Stream;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;

@Component
public class JsonSchemaParser {

    public SpecModel.WrapperType getWrapperType(JsonNode node) {
        return "array".equals(node.path("type").asString(null))
                ? SpecModel.WrapperType.LIST
                : SpecModel.WrapperType.OBJECT;
    }

    public List<ChildDefinition> extractChildren(JsonNode node, ParsingContext parsingContext) {
        if (node.has("properties")) {
            return getFromProperties(node);
        } else if (node.has("items") && node.get("items").has("properties")) {
            return getFromProperties(node.get("items"));
        } else if (hasComposition(node)) {
            return getFromCompositions(node, parsingContext);
        }
        return List.of();
    }

    private List<ChildDefinition> getFromProperties(JsonNode node) {
        JsonNode properties = node.get("properties");
        Set<String> requiredFields = extractRequiredFields(node);
        List<ChildDefinition> children = new ArrayList<>();
        properties
                .properties()
                .forEach(entry -> children.add(new ChildDefinition(
                        entry.getKey(), entry.getValue(), requiredFields.contains(entry.getKey()))));
        return children;
    }

    private List<ChildDefinition> getFromCompositions(JsonNode node, ParsingContext parsingContext) {
        List<ChildDefinition> children = new ArrayList<>();
        Stream.of("oneOf", "anyOf", "allOf").filter(node::has).forEach(compositionType -> {
            node.get(compositionType)
                    .forEach(childNode -> processCompositeNode(parsingContext, compositionType, childNode, children));
        });
        return children;
    }

    private void processCompositeNode(
            ParsingContext parsingContext, String compositionType, JsonNode childNode, List<ChildDefinition> children) {
        if (childNode.has("properties")) {
            childNode.get("properties").properties().forEach(entry -> {
                String propertyName = entry.getKey();
                boolean isDuplicate = children.stream().anyMatch(c -> c.name().equals(propertyName));
                if (!isDuplicate) {
                    children.add(new ChildDefinition(propertyName, entry.getValue(), false));
                    parsingContext.addWarning(
                            String.format(
                                    "Property '%s' was merged from '%s' composition", propertyName, compositionType),
                            JSON_SCHEMA_COMPOSITION_PROPERTY_MERGED);
                } else {
                    parsingContext.addWarning(
                            String.format(
                                    "Duplicate property '%s' inside '%s' composition was ignored",
                                    propertyName, compositionType),
                            JSON_SCHEMA_COMPOSITION_PROPERTY_DUPLICATE);
                }
            });
        }
    }

    private Set<String> extractRequiredFields(JsonNode node) {
        Set<String> requiredFields = new HashSet<>();
        JsonNode required = node.get("required");
        if (Objects.nonNull(required) && required.isArray()) {
            required.forEach(field -> requiredFields.add(field.asString(null)));
        }
        return requiredFields;
    }

    private boolean hasComposition(JsonNode node) {
        return node.has("allOf") || node.has("oneOf") || node.has("anyOf");
    }

    public record ChildDefinition(String name, JsonNode node, boolean isRequired) {}
}
