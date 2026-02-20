package com.voleksiienko.specforgeapi.infra.adapter.out.json.inner.type.impl;

import static com.voleksiienko.specforgeapi.core.domain.model.error.JsonMappingErrorCode.JSON_SCHEMA_TEMPORAL_FORMAT_DEFAULTED;

import com.voleksiienko.specforgeapi.core.domain.model.spec.SpecProperty;
import com.voleksiienko.specforgeapi.core.domain.model.spec.type.*;
import com.voleksiienko.specforgeapi.infra.adapter.out.json.inner.ParsingContext;
import com.voleksiienko.specforgeapi.infra.adapter.out.json.inner.type.SpecTypeCreator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;

@Component
public class StringSpecTypeCreator implements SpecTypeCreator {

    private static final Set<String> SUPPORTED_STRING_FORMATS = Set.of("email", "uuid");
    private static final String JSON_FORMAT_FIELD = "format";

    @Override
    public boolean supports(JsonNode node, String type) {
        return "string".equals(type);
    }

    @Override
    public SpecType createType(
            JsonNode node,
            ParsingContext parsingContext,
            List<String> examples,
            BiFunction<JsonNode, ParsingContext, List<SpecProperty>> propertyCreator) {
        return isEnum(node) ? buildEnumNodeType(node) : buildStringOrTemporalNodeType(node, examples, parsingContext);
    }

    private boolean isEnum(JsonNode node) {
        return node.has("enum")
                && node.get("enum").valueStream().map(JsonNode::asString).anyMatch(Objects::nonNull);
    }

    private SpecType buildEnumNodeType(JsonNode node) {
        return EnumSpecType.builder().values(getEnumValues(node)).build();
    }

    private Set<String> getEnumValues(JsonNode node) {
        return node.get("enum")
                .valueStream()
                .map(JsonNode::asString)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    private SpecType buildStringOrTemporalNodeType(
            JsonNode node, List<String> examples, ParsingContext parsingContext) {
        SpecType specType = mapToTemporalNodeType(node.path(JSON_FORMAT_FIELD).asString(null), parsingContext);
        if (Objects.isNull(specType)) {
            return StringSpecType.builder()
                    .format(node.has(JSON_FORMAT_FIELD) ? getFormatIfSupported(node) : null)
                    .minLength(node.has("minLength") ? node.get("minLength").asInt() : null)
                    .maxLength(node.has("maxLength") ? node.get("maxLength").asInt() : null)
                    .pattern(node.has("pattern") ? node.get("pattern").asString(null) : null)
                    .examples(examples)
                    .build();
        }
        return specType;
    }

    private StringSpecType.StringTypeFormat getFormatIfSupported(JsonNode node) {
        String format = node.get(JSON_FORMAT_FIELD).asString(null);
        return SUPPORTED_STRING_FORMATS.contains(format)
                ? StringSpecType.StringTypeFormat.valueOf(format.toUpperCase())
                : null;
    }

    private SpecType mapToTemporalNodeType(String format, ParsingContext parsingContext) {
        return switch (format) {
            case "date-time" -> {
                String defaultFormat = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";
                parsingContext.addWarning(
                        "Json format 'date-time' detected but no specific pattern provided, defaulting to ISO-8601: "
                                + defaultFormat,
                        JSON_SCHEMA_TEMPORAL_FORMAT_DEFAULTED);
                yield DateTimeSpecType.builder().format(defaultFormat).build();
            }
            case "date" -> {
                String defaultFormat = "yyyy-MM-dd";
                parsingContext.addWarning(
                        "Json format 'date' detected but no specific pattern provided, defaulting to ISO-8601: "
                                + defaultFormat,
                        JSON_SCHEMA_TEMPORAL_FORMAT_DEFAULTED);
                yield DateSpecType.builder().format(defaultFormat).build();
            }
            case "time" -> {
                String defaultFormat = "HH:mm:ss";
                parsingContext.addWarning(
                        "Json format 'time' detected but no specific pattern provided, defaulting to ISO-8601: "
                                + defaultFormat,
                        JSON_SCHEMA_TEMPORAL_FORMAT_DEFAULTED);
                yield TimeSpecType.builder().format(defaultFormat).build();
            }
            case null, default -> null;
        };
    }
}
