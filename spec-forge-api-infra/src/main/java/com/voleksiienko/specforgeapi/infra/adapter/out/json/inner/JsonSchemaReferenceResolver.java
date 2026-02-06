package com.voleksiienko.specforgeapi.infra.adapter.out.json.inner;

import static com.voleksiienko.specforgeapi.core.domain.model.error.JsonMappingErrorCode.JSON_SCHEMA_CIRCULAR_REF_FOUND;
import static com.voleksiienko.specforgeapi.core.domain.model.error.JsonMappingErrorCode.JSON_SCHEMA_UNRESOLVED_REF_FOUND;

import com.voleksiienko.specforgeapi.core.application.exception.ConversionException;
import java.util.*;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.ObjectNode;

@Service
public class JsonSchemaReferenceResolver {

    public JsonNode resolveRefs(JsonNode rootNode) {
        Map<String, JsonNode> refRegistry = new HashMap<>();
        if (rootNode.has("definitions")) {
            rootNode.get("definitions")
                    .properties()
                    .forEach(entry -> refRegistry.put("#/definitions/" + entry.getKey(), entry.getValue()));
        }
        if (rootNode.has("$defs")) {
            rootNode.get("$defs")
                    .properties()
                    .forEach(entry -> refRegistry.put("#/$defs/" + entry.getKey(), entry.getValue()));
        }
        return resolveRefs(rootNode, refRegistry, new HashSet<>());
    }

    private JsonNode resolveRefs(JsonNode node, Map<String, JsonNode> refRegistry, Set<String> visitedRefs) {
        if (node.isObject() && node.has("$ref")) {
            String ref = node.get("$ref").asString(null);
            if (visitedRefs.contains(ref)) {
                throw new ConversionException("Circular $ref detected: " + ref, JSON_SCHEMA_CIRCULAR_REF_FOUND);
            }
            JsonNode resolved = refRegistry.get(ref);
            if (Objects.isNull(resolved)) {
                throw new ConversionException("Unresolved $ref: " + ref, JSON_SCHEMA_UNRESOLVED_REF_FOUND);
            }
            visitedRefs.add(ref);
            JsonNode flattened = resolveRefs(resolved, refRegistry, visitedRefs);
            visitedRefs.remove(ref);
            return flattened;
        } else if (node.isObject()) {
            ObjectNode copy = (ObjectNode) node.deepCopy();
            copy.properties()
                    .forEach(
                            entry -> copy.set(entry.getKey(), resolveRefs(entry.getValue(), refRegistry, visitedRefs)));
            return copy;
        } else if (node.isArray()) {
            ArrayNode copy = (ArrayNode) node.deepCopy();
            for (int i = 0; i < copy.size(); i++) {
                copy.set(i, resolveRefs(copy.get(i), refRegistry, visitedRefs));
            }
            return copy;
        }
        return node;
    }
}
