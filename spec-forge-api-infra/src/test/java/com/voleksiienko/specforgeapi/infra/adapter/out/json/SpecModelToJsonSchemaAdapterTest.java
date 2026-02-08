package com.voleksiienko.specforgeapi.infra.adapter.out.json;

import com.voleksiienko.specforgeapi.core.domain.model.spec.SpecModel;
import com.voleksiienko.specforgeapi.core.domain.model.spec.SpecProperty;
import com.voleksiienko.specforgeapi.core.domain.model.spec.type.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

class SpecModelToJsonSchemaAdapterTest {

    private final SpecModelToJsonSchemaAdapter adapter = new SpecModelToJsonSchemaAdapter();

    @Test
    void shouldGenerateSchemaFromPrimitivesWithConstraints() throws Exception {
        var properties = List.of(
                createProp("boolField", new BooleanSpecType()),
                createProp(
                        "intField",
                        IntegerSpecType.builder().minimum(100L).maximum(200L).build()),
                createProp(
                        "doubleField",
                        DoubleSpecType.builder().minimum(10.5).maximum(20.5).build()),
                createProp(
                        "decimalField",
                        DecimalSpecType.builder()
                                .scale(2)
                                .minimum(new BigDecimal("99.99"))
                                .build()),
                createProp(
                        "dateField", DateSpecType.builder().format("yyyy-MM-dd").build()),
                createProp(
                        "dateTimeField",
                        DateTimeSpecType.builder().format("yyyy-MM-dd HH:mm").build()),
                createProp(
                        "timeField", TimeSpecType.builder().format("HH:mm:ss").build()));
        SpecModel specModel = SpecModel.builder()
                .wrapperType(SpecModel.WrapperType.OBJECT)
                .properties(properties)
                .build();

        String schema = adapter.map(specModel);

        String expectedSchema = """
            {
              "$schema": "https://json-schema.org/draft/2020-12/schema#",
              "type": "object",
              "properties": {
                "boolField": { "type": "boolean" },
                "intField": { "type": "integer", "minimum": 100, "maximum": 200 },
                "doubleField": { "type": "number", "minimum": 10.5, "maximum": 20.5 },
                "decimalField": { "type": "number", "minimum": 99.99 },
                "dateField": { "type": "string", "format": "date" },
                "dateTimeField": { "type": "string", "format": "date-time" },
                "timeField": { "type": "string", "format": "time" }
              }
            }
            """;
        JSONAssert.assertEquals(expectedSchema, schema, JSONCompareMode.LENIENT);
    }

    @Test
    void shouldHandleStringFormatsAndValidationInSchema() throws Exception {
        var properties = List.of(
                createProp(
                        "status",
                        EnumSpecType.builder()
                                .values(Set.of("active", "disabled"))
                                .build()),
                createProp(
                        "email",
                        StringSpecType.builder()
                                .format(StringSpecType.StringTypeFormat.EMAIL)
                                .build()),
                createProp(
                        "uuid",
                        StringSpecType.builder()
                                .format(StringSpecType.StringTypeFormat.UUID)
                                .build()),
                createProp(
                        "patterned",
                        StringSpecType.builder()
                                .pattern("^[A-Z]+$")
                                .minLength(2)
                                .build()));
        SpecModel specModel = SpecModel.builder()
                .wrapperType(SpecModel.WrapperType.OBJECT)
                .properties(properties)
                .build();

        String schema = adapter.map(specModel);

        String expectedSchema = """
            {
              "properties": {
                "status": { "type": "string", "enum": ["active", "disabled"] },
                "email": { "type": "string", "format": "email" },
                "uuid": { "type": "string", "format": "uuid" },
                "patterned": { "type": "string", "pattern": "^[A-Z]+$", "minLength": 2 }
              }
            }
            """;
        JSONAssert.assertEquals(expectedSchema, schema, JSONCompareMode.LENIENT);
    }

    @Test
    void shouldGenerateSchemaForComplexNestedStructures() throws Exception {
        var listType = ListSpecType.builder()
                .valueType(IntegerSpecType.builder().minimum(42L).build())
                .build();
        var mapType = MapSpecType.builder()
                .keyType(StringSpecType.builder().build())
                .valueType(listType)
                .build();
        SpecModel specModel = SpecModel.builder()
                .wrapperType(SpecModel.WrapperType.OBJECT)
                .properties(List.of(createProp("complexMap", mapType)))
                .build();

        String schema = adapter.map(specModel);

        String expectedSchema = """
            {
              "properties": {
                "complexMap": {
                  "type": "object",
                  "additionalProperties": {
                    "type": "array",
                    "items": {
                      "type": "integer",
                      "minimum": 42
                    }
                  }
                }
              }
            }
            """;
        JSONAssert.assertEquals(expectedSchema, schema, JSONCompareMode.LENIENT);
    }

    @Test
    void shouldGenerateSchemaForNestedObjects() throws Exception {
        var childProp = SpecProperty.builder()
                .name("nestedField")
                .type(StringSpecType.builder().build())
                .required(true)
                .deprecated(true)
                .description("Nested field")
                .build();
        var objectType = ObjectSpecType.builder().children(List.of(childProp)).build();
        SpecModel specModel = SpecModel.builder()
                .wrapperType(SpecModel.WrapperType.OBJECT)
                .properties(List.of(createProp("parent", objectType)))
                .build();

        String schema = adapter.map(specModel);

        String expectedSchema = """
            {
              "properties": {
                "parent": {
                  "type": "object",
                  "properties": {
                    "nestedField": { "type": "string", "deprecated": true, "description": "Nested field" }
                  },
                  "required": ["nestedField"],
                  "additionalProperties": false
                }
              }
            }
            """;
        JSONAssert.assertEquals(expectedSchema, schema, JSONCompareMode.LENIENT);
    }

    @Test
    void shouldGenerateArraySchemaWhenWrapperIsList() throws Exception {
        SpecModel specModel = SpecModel.builder()
                .wrapperType(SpecModel.WrapperType.LIST)
                .properties(
                        List.of(createProp("itemName", StringSpecType.builder().build())))
                .build();

        String schema = adapter.map(specModel);

        String expectedSchema = """
            {
              "type": "array",
              "items": {
                "type": "object",
                "properties": {
                  "itemName": { "type": "string" }
                },
                "additionalProperties": false
              }
            }
            """;
        JSONAssert.assertEquals(expectedSchema, schema, JSONCompareMode.LENIENT);
    }

    private SpecProperty createProp(String name, SpecType type) {
        return SpecProperty.builder().name(name).type(type).build();
    }
}
