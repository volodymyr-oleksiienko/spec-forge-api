package com.voleksiienko.specforgeapi.infra.adapter.out.json.inner.type;

import static com.voleksiienko.specforgeapi.core.domain.model.error.JsonMappingErrorCode.JSON_SCHEMA_PROPERTY_TYPE_INFERRED;
import static com.voleksiienko.specforgeapi.core.domain.model.error.JsonMappingErrorCode.JSON_SCHEMA_TYPE_DEFAULTED_TO_STRING;

import com.voleksiienko.specforgeapi.core.common.Asserts;
import com.voleksiienko.specforgeapi.core.domain.model.spec.type.SpecType;
import com.voleksiienko.specforgeapi.infra.adapter.out.json.inner.ParsingContext;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;

@Component
public class SpecTypeCreatorFacade {

    private List<SpecTypeCreator> specTypeCreators;

    public SpecType create(JsonNode node, ParsingContext parsingContext) {
        String type = getJsonNodeType(node, parsingContext);
        return specTypeCreators.stream()
                .filter(nodeMapper -> nodeMapper.supports(node, type))
                .findFirst()
                .map(nodeMapper -> nodeMapper.createType(node, parsingContext))
                .orElse(null);
    }

    private String getJsonNodeType(JsonNode node, ParsingContext parsingContext) {
        JsonNode typeNode = node.path("type");

        if (!typeNode.isMissingNode() && !typeNode.isNull()) {
            Stream<JsonNode> typeStream =
                    typeNode.isArray() ? StreamSupport.stream(typeNode.spliterator(), false) : Stream.of(typeNode);
            Optional<String> explicitType = typeStream
                    .map(JsonNode::asString)
                    .filter(val -> Asserts.isNotBlank(val) && !"null".equals(val))
                    .findFirst();
            if (explicitType.isPresent()) {
                return explicitType.get();
            }
        }

        String inferredType = inferTypeFromKeywords(node);
        if (inferredType != null) {
            parsingContext.addWarning(
                    String.format(
                            "Type definition is missing, inferred type '%s' based on schema keywords", inferredType),
                    JSON_SCHEMA_PROPERTY_TYPE_INFERRED);
            return inferredType;
        }

        parsingContext.addWarning(
                "Type definition is missing and cannot be inferred, defaulting to 'string'",
                JSON_SCHEMA_TYPE_DEFAULTED_TO_STRING);
        return "string";
    }

    private String inferTypeFromKeywords(JsonNode node) {
        if (node.has("properties") || node.has("additionalProperties")) {
            return "object";
        }
        if (node.has("items")) {
            return "array";
        }
        if (node.has("enum") || node.has("pattern") || node.has("format") || node.has("minLength")) {
            return "string";
        }
        return null;
    }

    @Lazy
    @Autowired
    public void setSpecTypeCreators(List<SpecTypeCreator> specTypeCreators) {
        this.specTypeCreators = specTypeCreators;
    }
}
