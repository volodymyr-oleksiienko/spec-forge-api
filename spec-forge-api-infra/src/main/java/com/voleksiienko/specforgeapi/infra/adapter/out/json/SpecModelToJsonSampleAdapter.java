package com.voleksiienko.specforgeapi.infra.adapter.out.json;

import com.voleksiienko.specforgeapi.core.application.exception.ConversionException;
import com.voleksiienko.specforgeapi.core.application.port.out.json.SpecModelToJsonSamplePort;
import com.voleksiienko.specforgeapi.core.common.Asserts;
import com.voleksiienko.specforgeapi.core.domain.model.spec.SpecModel;
import com.voleksiienko.specforgeapi.core.domain.model.spec.SpecProperty;
import com.voleksiienko.specforgeapi.core.domain.model.spec.type.*;
import com.voleksiienko.specforgeapi.infra.adapter.in.web.dto.response.ApiErrorCode;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.ObjectNode;

@Service
public class SpecModelToJsonSampleAdapter implements SpecModelToJsonSamplePort {

    private static final JsonMapper JSON_MAPPER = JsonMapper.builder().build();
    private static final Map<StringSpecType.StringTypeFormat, String> STRING_FORMAT_TO_EXAMPLE = Map.of(
            StringSpecType.StringTypeFormat.EMAIL, "user@example.com",
            StringSpecType.StringTypeFormat.UUID, "1b9d6bcd-bbfd-4b2d-9b5d-ab8dfbbd4bed");

    @Override
    public String map(SpecModel specModel) {
        ObjectNode root = JSON_MAPPER.createObjectNode();
        specModel
                .getProperties()
                .forEach(specProperty -> root.set(
                        specProperty.getName(),
                        JSON_MAPPER.valueToTree(generatePropertySample(specProperty.getType()))));
        if (SpecModel.WrapperType.OBJECT == specModel.getWrapperType()) {
            return root.toString();
        }
        ArrayNode arrayNode = JSON_MAPPER.createArrayNode();
        arrayNode.add(root);
        return arrayNode.toString();
    }

    private Object generatePropertySample(SpecType specType) {
        if (specType instanceof PrimitiveSpecType primitiveSpecType
                && Asserts.isNotEmpty(primitiveSpecType.getExamples())) {
            return parseExample(primitiveSpecType.getExamples().getFirst(), specType);
        }
        return switch (specType) {
            case StringSpecType stringSpecType -> generateString(stringSpecType);
            case ObjectSpecType t -> generateObject(t.getChildren());
            case ListSpecType listSpecType -> generateList(listSpecType);
            case MapSpecType mapSpecType -> generateMap(mapSpecType);
            default ->
                throw new ConversionException(
                        "SpecType [%s] must have already generated examples"
                                .formatted(specType.getClass().getName()),
                        ApiErrorCode.INTERNAL);
        };
    }

    private Object parseExample(String example, SpecType specType) {
        try {
            return switch (specType) {
                case BooleanSpecType ignored -> Boolean.parseBoolean(example);
                case IntegerSpecType ignored -> Integer.parseInt(example);
                case DoubleSpecType ignored -> Double.parseDouble(example);
                case DecimalSpecType ignored -> new BigDecimal(example);
                default -> example;
            };
        } catch (Exception _) {
            return example;
        }
    }

    private String generateString(StringSpecType type) {
        return Objects.nonNull(type.getFormat()) ? STRING_FORMAT_TO_EXAMPLE.get(type.getFormat()) : "string";
    }

    private JsonNode generateObject(List<SpecProperty> children) {
        ObjectNode objectNode = JSON_MAPPER.createObjectNode();
        children.forEach(child ->
                objectNode.set(child.getName(), JSON_MAPPER.valueToTree(generatePropertySample(child.getType()))));
        return objectNode;
    }

    private JsonNode generateList(ListSpecType type) {
        ArrayNode arrayNode = JSON_MAPPER.createArrayNode();
        arrayNode.add(JSON_MAPPER.valueToTree(generatePropertySample(type.getValueType())));
        return arrayNode;
    }

    private JsonNode generateMap(MapSpecType type) {
        ObjectNode mapNode = JSON_MAPPER.createObjectNode();
        String keySample = String.valueOf(generatePropertySample(type.getKeyType()));
        JsonNode valueSample = JSON_MAPPER.valueToTree(generatePropertySample(type.getValueType()));
        mapNode.set(keySample, valueSample);
        return mapNode;
    }
}
