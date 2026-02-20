package com.voleksiienko.specforgeapi.infra.adapter.out.json;

import static org.assertj.core.api.Assertions.assertThat;

import com.voleksiienko.specforgeapi.core.domain.model.spec.SpecModel;
import com.voleksiienko.specforgeapi.core.domain.model.spec.SpecProperty;
import com.voleksiienko.specforgeapi.core.domain.model.spec.type.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

class SpecModelToJsonSampleAdapterTest {

    private final SpecModelToJsonSampleAdapter adapter = new SpecModelToJsonSampleAdapter();

    @Test
    void shouldGenerateJsonFromPrimitivesWithExamples() throws Exception {
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
                        "stringField",
                        StringSpecType.builder().examples(List.of("my-string")).build()),
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

        String json = adapter.map(specModel);

        String expectedJson = """
            {
              "boolField": true,
              "intField": 100,
              "doubleField": 10.5,
              "decimalField": 99.99,
              "stringField": "my-string"
            }
            """;
        JSONAssert.assertEquals(expectedJson, json, JSONCompareMode.LENIENT);
        assertThat(json).contains("dateField");
        assertThat(json).contains("dateTimeField");
        assertThat(json).contains("timeField");
    }

    @Test
    void shouldHandleStringFormatsDefaults() throws Exception {
        var properties = List.of(
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
                createProp("default", StringSpecType.builder().build()));
        SpecModel specModel = SpecModel.builder()
                .wrapperType(SpecModel.WrapperType.OBJECT)
                .properties(properties)
                .build();

        String json = adapter.map(specModel);

        String expectedJson = """
            {
              "email": "user@example.com",
              "uuid": "1b9d6bcd-bbfd-4b2d-9b5d-ab8dfbbd4bed",
              "default": "string"
            }
            """;
        JSONAssert.assertEquals(expectedJson, json, JSONCompareMode.STRICT);
    }

    @Test
    void shouldGenerateComplexNestedStructures() throws Exception {
        var listType = ListSpecType.builder()
                .valueType(IntegerSpecType.builder().minimum(42L).maximum(42L).build())
                .build();
        var mapType = MapSpecType.builder()
                .keyType(EnumSpecType.builder().values(Set.of("KEY_A")).build())
                .valueType(listType)
                .build();
        SpecModel specModel = SpecModel.builder()
                .wrapperType(SpecModel.WrapperType.OBJECT)
                .properties(List.of(createProp("complexMap", mapType)))
                .build();

        String json = adapter.map(specModel);

        String expectedJson = """
            {
              "complexMap": {
                "KEY_A": [42]
              }
            }
            """;
        JSONAssert.assertEquals(expectedJson, json, JSONCompareMode.STRICT);
    }

    @Test
    void shouldGenerateNestedObjects() throws Exception {
        var childProp = createProp(
                "nestedField",
                StringSpecType.builder().examples(List.of("deep")).build());
        var objectType = ObjectSpecType.builder().children(List.of(childProp)).build();

        SpecModel specModel = SpecModel.builder()
                .wrapperType(SpecModel.WrapperType.OBJECT)
                .properties(List.of(createProp("parent", objectType)))
                .build();

        String json = adapter.map(specModel);

        String expectedJson = """
            {
              "parent": {
                "nestedField": "deep"
              }
            }
            """;
        JSONAssert.assertEquals(expectedJson, json, JSONCompareMode.STRICT);
    }

    @Test
    void shouldGenerateJsonArrayWhenWrapperIsList() throws Exception {
        SpecModel specModel = SpecModel.builder()
                .wrapperType(SpecModel.WrapperType.LIST)
                .properties(List.of(createProp(
                        "itemName",
                        StringSpecType.builder()
                                .examples(List.of("example-item"))
                                .build())))
                .build();

        String json = adapter.map(specModel);

        String expectedJson = """
            [
              {
                "itemName": "example-item"
              }
            ]
            """;
        JSONAssert.assertEquals(expectedJson, json, JSONCompareMode.STRICT);
    }

    private SpecProperty createProp(String name, SpecType type) {
        return SpecProperty.builder().name(name).type(type).build();
    }
}
