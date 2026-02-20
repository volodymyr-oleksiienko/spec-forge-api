package com.voleksiienko.specforgeapi.infra.adapter.out.json;

import static com.voleksiienko.specforgeapi.core.domain.model.error.JsonMappingErrorCode.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.voleksiienko.specforgeapi.core.application.exception.ConversionException;
import com.voleksiienko.specforgeapi.core.domain.model.conversion.ConversionResult;
import com.voleksiienko.specforgeapi.core.domain.model.spec.SpecModel;
import com.voleksiienko.specforgeapi.core.domain.model.spec.SpecProperty;
import com.voleksiienko.specforgeapi.core.domain.model.spec.type.*;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class JsonSchemaToSpecModelAdapterIntegrationTest {

    @Autowired
    private JsonSchemaToSpecModelAdapter adapter;

    @Test
    void shouldMapAllPrimitiveTypesAndConstraints() {
        String json = """
            {
              "type": "object",
              "required": ["mandatoryField", "nullableField"],
              "properties": {
                "mandatoryField": { "type": "string", "minLength": 5, "maxLength": 10, "pattern": "^[a-z]+$" },
                "nullableField": { "type": ["string", "null"] },
                "deprecatedField": { "type": "string", "deprecated": true },
                "enumField": { "type": "string", "enum": ["A", "B"] },
                "dateTimeField": { "type": "string", "format": "date-time" },
                "uuidField": { "type": "string", "format": "uuid" },
                "intField": { "type": "integer", "minimum": 1, "maximum": 10 },
                "exclusiveIntField": { "type": "integer", "exclusiveMinimum": 0, "exclusiveMaximum": 100 },
                "numberField": { "type": "number", "minimum": 1.5 },
                "boolField": { "type": "boolean" },
                "simpleArray": { "type": "array", "items": { "type": "integer" }, "minItems": 1 },
                "stringMap": { "type": "object", "additionalProperties": { "type": "string" } },
                "exampleField": { "type": "string", "examples": ["ex1"], "description": "desc" }
              }
            }
            """;

        ConversionResult result = adapter.map(json);
        SpecModel model = result.model();
        List<SpecProperty> props = model.getProperties();

        // 1. Strings & Constraints
        SpecProperty mandatory = getProp(props, "mandatoryField");
        assertThat(mandatory.isRequired()).isTrue();
        assertThat(mandatory.getType()).isInstanceOf(StringSpecType.class);
        StringSpecType stringType = (StringSpecType) mandatory.getType();
        assertThat(stringType.getMinLength()).isEqualTo(5);
        assertThat(stringType.getMaxLength()).isEqualTo(10);
        assertThat(stringType.getPattern()).isEqualTo("^[a-z]+$");

        // 2. Nullable & Deprecated
        SpecProperty nullable = getProp(props, "nullableField");
        assertThat(nullable.isRequired()).isFalse();

        SpecProperty deprecated = getProp(props, "deprecatedField");
        assertThat(deprecated.isDeprecated()).isTrue();

        // 3. Enums
        SpecProperty enumProp = getProp(props, "enumField");
        assertThat(enumProp.getType()).isInstanceOf(EnumSpecType.class);
        assertThat(((EnumSpecType) enumProp.getType()).getValues()).containsExactlyInAnyOrder("A", "B");

        // 4. Formats (Date-Time / UUID)
        assertThat(getProp(props, "dateTimeField").getType()).isInstanceOf(DateTimeSpecType.class);
        assertThat(getProp(props, "uuidField").getType())
                .isInstanceOf(StringSpecType.class)
                .extracting("format")
                .isEqualTo(StringSpecType.StringTypeFormat.UUID);

        // 5. Integers (Inclusive vs Exclusive)
        IntegerSpecType intType = (IntegerSpecType) getProp(props, "intField").getType();
        assertThat(intType.getMinimum()).isEqualTo(1L);
        assertThat(intType.getMaximum()).isEqualTo(10L);

        IntegerSpecType exclusiveInt =
                (IntegerSpecType) getProp(props, "exclusiveIntField").getType();
        assertThat(exclusiveInt.getMinimum()).isEqualTo(1L);
        assertThat(exclusiveInt.getMaximum()).isEqualTo(99L);

        // 6. Arrays
        ListSpecType listType = (ListSpecType) getProp(props, "simpleArray").getType();
        assertThat(listType.getMinItems()).isEqualTo(1);
        assertThat(listType.getValueType()).isInstanceOf(IntegerSpecType.class);

        // 7. Maps
        MapSpecType mapType = (MapSpecType) getProp(props, "stringMap").getType();
        assertThat(mapType.getKeyType()).isInstanceOf(StringSpecType.class);
        assertThat(mapType.getValueType()).isInstanceOf(StringSpecType.class);

        // 8. Metadata
        SpecProperty exampleProp = getProp(props, "exampleField");
        assertThat(((PrimitiveSpecType) exampleProp.getType()).getExamples()).contains("ex1");
        assertThat(exampleProp.getDescription()).isEqualTo("desc");
    }

    @Test
    void shouldResolveReferences() {
        String json = """
            {
              "type": "object",
              "properties": {
                "ref1": { "$ref": "#/definitions/OldStyle" },
                "ref2": { "$ref": "#/$defs/NewStyle" },
                "arrayRef": {
                    "type": "array",
                    "items": { "$ref": "#/$defs/NewStyle" }
                }
              },
              "definitions": {
                "OldStyle": { "type": "integer" }
              },
              "$defs": {
                "NewStyle": { "type": "boolean" }
              }
            }
            """;

        SpecModel model = adapter.map(json).model();
        List<SpecProperty> props = model.getProperties();

        assertThat(getProp(props, "ref1").getType()).isInstanceOf(IntegerSpecType.class);
        assertThat(getProp(props, "ref2").getType()).isInstanceOf(BooleanSpecType.class);

        ListSpecType arrayType = (ListSpecType) getProp(props, "arrayRef").getType();
        assertThat(arrayType.getValueType()).isInstanceOf(BooleanSpecType.class);
    }

    @Test
    void shouldHandleCompositionAndWarnings() {
        String json = """
            {
              "type": "object",
              "allOf": [
                {
                    "properties": {
                        "shared": { "type": "string" },
                        "unique1": { "type": "integer" }
                    }
                },
                {
                    "properties": {
                        "shared": { "type": "string" },
                        "unique2": { "type": "boolean" }
                    }
                }
              ]
            }
            """;

        ConversionResult result = adapter.map(json);
        SpecModel model = result.model();

        assertThat(model.getProperties())
                .extracting(SpecProperty::getName)
                .containsExactlyInAnyOrder("shared", "unique1", "unique2");

        assertThat(result.warnings())
                .extracting("errorCode")
                .contains(JSON_SCHEMA_COMPOSITION_PROPERTY_MERGED, JSON_SCHEMA_COMPOSITION_PROPERTY_DUPLICATE);
    }

    @Test
    void shouldInferTypes() {
        String json = """
            {
              "properties": {
                "inferredObj": { "properties": { "a": {} } },
                "inferredArr": { "items": {} },
                "inferredStr": { "minLength": 1 },
                "unknown": {}
              }
            }
            """;

        ConversionResult result = adapter.map(json);
        List<SpecProperty> props = result.model().getProperties();

        assertThat(getProp(props, "inferredObj").getType()).isInstanceOf(ObjectSpecType.class);
        assertThat(getProp(props, "inferredArr").getType()).isInstanceOf(ListSpecType.class);
        assertThat(getProp(props, "inferredStr").getType()).isInstanceOf(StringSpecType.class);
        assertThat(getProp(props, "unknown").getType()).isInstanceOf(StringSpecType.class);

        assertThat(result.warnings())
                .extracting("errorCode")
                .contains(JSON_SCHEMA_PROPERTY_TYPE_INFERRED, JSON_SCHEMA_TYPE_DEFAULTED_TO_STRING);
    }

    @Test
    void shouldThrowWhenCircularRef() {
        String json = """
            {
              "properties": {
                "loop": { "$ref": "#/definitions/Loop" }
              },
              "definitions": {
                "Loop": { "$ref": "#/definitions/Loop" }
              }
            }
            """;

        assertThatThrownBy(() -> adapter.map(json))
                .isInstanceOf(ConversionException.class)
                .extracting("errorCode")
                .isEqualTo(JSON_SCHEMA_CIRCULAR_REF_FOUND);
    }

    @Test
    void shouldThrowWhenMissingRef() {
        String json = """
            { "properties": { "missing": { "$ref": "#/definitions/NonExistent" } } }
            """;

        assertThatThrownBy(() -> adapter.map(json))
                .isInstanceOf(ConversionException.class)
                .extracting("errorCode")
                .isEqualTo(JSON_SCHEMA_UNRESOLVED_REF_FOUND);
    }

    @Test
    void shouldThrowWhenMalformedJson() {
        String invalidJson = "{ \"type\": \"object\", ";

        assertThatThrownBy(() -> adapter.map(invalidJson))
                .isInstanceOf(ConversionException.class)
                .extracting("errorCode")
                .isEqualTo(JSON_SCHEMA_PARSING_FAILED);
    }

    @Test
    void shouldHandleTopLevelArray() {
        String json = """
            {
                "type": "array",
                "items": {
                    "type": "object",
                    "properties": {
                        "id": { "type": "string" },
                        "count": { "type": "integer" }
                    }
                }
            }
            """;

        SpecModel model = adapter.map(json).model();

        assertThat(model.getWrapperType()).isEqualTo(SpecModel.WrapperType.LIST);

        assertThat(model.getProperties()).hasSize(2);
        assertThat(getProp(model.getProperties(), "id").getType()).isInstanceOf(StringSpecType.class);
        assertThat(getProp(model.getProperties(), "count").getType()).isInstanceOf(IntegerSpecType.class);
    }

    private SpecProperty getProp(List<SpecProperty> props, String name) {
        return props.stream()
                .filter(p -> p.getName().equals(name))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Property [%s] not found".formatted(name)));
    }
}
