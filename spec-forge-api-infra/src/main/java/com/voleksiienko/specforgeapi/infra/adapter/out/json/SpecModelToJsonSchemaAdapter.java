package com.voleksiienko.specforgeapi.infra.adapter.out.json;

import static com.voleksiienko.specforgeapi.core.common.Asserts.isNotEmpty;
import static com.voleksiienko.specforgeapi.core.domain.model.spec.type.StringSpecType.StringTypeFormat.EMAIL;
import static com.voleksiienko.specforgeapi.core.domain.model.spec.type.StringSpecType.StringTypeFormat.UUID;

import com.voleksiienko.specforgeapi.core.application.port.out.json.SpecModelToJsonSchemaPort;
import com.voleksiienko.specforgeapi.core.domain.model.spec.SpecModel;
import com.voleksiienko.specforgeapi.core.domain.model.spec.SpecProperty;
import com.voleksiienko.specforgeapi.core.domain.model.spec.type.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.springframework.stereotype.Service;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.ObjectNode;

@Service
public class SpecModelToJsonSchemaAdapter implements SpecModelToJsonSchemaPort {

    private static final JsonMapper JSON_MAPPER = JsonMapper.builder().build();
    private static final Map<StringSpecType.StringTypeFormat, String> STRING_FORMAT_MAPPER =
            Map.of(EMAIL, "email", UUID, "uuid");
    private static final String SCHEMA_VERSION = "https://json-schema.org/draft/2020-12/schema#";

    @Override
    public String map(SpecModel specModel) {
        ObjectNode schema = JSON_MAPPER.createObjectNode();
        schema.put("$schema", SCHEMA_VERSION);
        schema.put("title", "Root Schema");
        if (SpecModel.WrapperType.LIST == specModel.getWrapperType()) {
            schema.put("type", "array");
            ObjectNode itemsSchema = JSON_MAPPER.createObjectNode();
            itemsSchema.put("type", "object");
            buildProperties(itemsSchema, specModel.getProperties());
            schema.set("items", itemsSchema);
        } else {
            schema.put("type", "object");
            buildProperties(schema, specModel.getProperties());
        }
        return schema.toString();
    }

    private void buildProperties(ObjectNode parentSchema, List<SpecProperty> specProperties) {
        ObjectNode propertiesNode = JSON_MAPPER.createObjectNode();
        ArrayNode requiredNode = JSON_MAPPER.createArrayNode();
        specProperties.forEach(specProperty -> {
            ObjectNode propertySchema = convertToObjectNode(specProperty);
            propertiesNode.set(specProperty.getName(), propertySchema);
            if (specProperty.isRequired()) {
                requiredNode.add(specProperty.getName());
            }
        });
        parentSchema.set("properties", propertiesNode);
        if (!requiredNode.isEmpty()) {
            parentSchema.set("required", requiredNode);
        }
        parentSchema.put("additionalProperties", false);
    }

    private ObjectNode convertToObjectNode(SpecProperty specProperty) {
        ObjectNode nodeSchema = JSON_MAPPER.createObjectNode();
        if (Objects.nonNull(specProperty.getDescription())) {
            nodeSchema.put("description", specProperty.getDescription());
        }
        if (specProperty.isDeprecated()) {
            nodeSchema.put("deprecated", true);
        }
        enrichSchemaWithType(nodeSchema, specProperty.getType());
        addExamplesIfPresent(nodeSchema, specProperty.getType());
        return nodeSchema;
    }

    private void addExamplesIfPresent(ObjectNode schema, SpecType type) {
        if (type instanceof PrimitiveSpecType primitiveType && isNotEmpty(primitiveType.getExamples())) {
            ArrayNode examplesArray = schema.putArray("examples");
            for (String example : primitiveType.getExamples()) {
                addTypedExample(examplesArray, example, type);
            }
        }
    }

    private void addTypedExample(ArrayNode arrayNode, String example, SpecType type) {
        try {
            switch (type) {
                case BooleanSpecType _ -> arrayNode.add(Boolean.parseBoolean(example));
                case IntegerSpecType _ -> arrayNode.add(Long.parseLong(example));
                case DoubleSpecType _ -> arrayNode.add(Double.parseDouble(example));
                case DecimalSpecType _ -> arrayNode.add(new BigDecimal(example));
                case null, default -> arrayNode.add(example);
            }
        } catch (Exception _) {
            arrayNode.add(example);
        }
    }

    private void enrichSchemaWithType(ObjectNode schema, SpecType type) {
        switch (type) {
            case BooleanSpecType ignored -> schema.put("type", "boolean");
            case IntegerSpecType t -> enrichInteger(schema, t);
            case DoubleSpecType t -> enrichDouble(schema, t);
            case DecimalSpecType t -> enrichDecimal(schema, t);
            case StringSpecType t -> enrichString(schema, t);
            case DateSpecType ignored -> enrichTemporal(schema, "date");
            case TimeSpecType ignored -> enrichTemporal(schema, "time");
            case DateTimeSpecType ignored -> enrichTemporal(schema, "date-time");
            case EnumSpecType t -> enrichEnum(schema, t);
            case ObjectSpecType t -> enrichObject(schema, t);
            case ListSpecType t -> enrichList(schema, t);
            case MapSpecType t -> enrichMap(schema, t);
        }
    }

    private void enrichInteger(ObjectNode schema, IntegerSpecType t) {
        schema.put("type", "integer");
        setValue(schema, "minimum", t.getMinimum());
        setValue(schema, "maximum", t.getMaximum());
    }

    private void enrichDouble(ObjectNode schema, DoubleSpecType t) {
        schema.put("type", "number");
        setValue(schema, "minimum", t.getMinimum());
        setValue(schema, "maximum", t.getMaximum());
    }

    private void enrichDecimal(ObjectNode schema, DecimalSpecType t) {
        schema.put("type", "number");
        setValue(schema, "minimum", t.getMinimum());
        setValue(schema, "maximum", t.getMaximum());
    }

    private void enrichTemporal(ObjectNode schema, String format) {
        schema.put("type", "string");
        schema.put("format", format);
    }

    private void enrichEnum(ObjectNode schema, EnumSpecType t) {
        schema.put("type", "string");
        ArrayNode enumArr = schema.putArray("enum");
        t.getValues().forEach(enumArr::add);
    }

    private void enrichString(ObjectNode schema, StringSpecType t) {
        schema.put("type", "string");
        setValue(schema, "pattern", t.getPattern());
        setValue(schema, "minLength", t.getMinLength());
        setValue(schema, "maxLength", t.getMaxLength());
        Optional.ofNullable(t.getFormat())
                .ifPresent(format -> setValue(schema, "format", STRING_FORMAT_MAPPER.get(format)));
    }

    private void enrichObject(ObjectNode schema, ObjectSpecType t) {
        schema.put("type", "object");
        buildProperties(schema, t.getChildren());
    }

    private void enrichList(ObjectNode schema, ListSpecType t) {
        schema.put("type", "array");
        setValue(schema, "minItems", t.getMinItems());
        setValue(schema, "maxItems", t.getMaxItems());
        ObjectNode itemsSchema = JSON_MAPPER.createObjectNode();
        enrichSchemaWithType(itemsSchema, t.getValueType());
        schema.set("items", itemsSchema);
    }

    private void enrichMap(ObjectNode schema, MapSpecType t) {
        schema.put("type", "object");
        ObjectNode valueSchema = JSON_MAPPER.createObjectNode();
        enrichSchemaWithType(valueSchema, t.getValueType());
        schema.set("additionalProperties", valueSchema);
    }

    private void setValue(ObjectNode schema, String name, Object value) {
        if (Objects.nonNull(value)) {
            schema.putPOJO(name, value);
        }
    }
}
