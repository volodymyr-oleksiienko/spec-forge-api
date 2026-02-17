package com.voleksiienko.specforgeapi.infra.adapter.out.json;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.voleksiienko.specforgeapi.core.application.exception.ConversionException;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

class JsonSampleToJsonSchemaAdapterTest {

    private static final String SCHEMA_ID = "https://json-schema.org/draft/2020-12/schema#";

    private final JsonSampleToJsonSchemaAdapter adapter = new JsonSampleToJsonSchemaAdapter();

    @Test
    void shouldMatchFullSchema() throws JSONException {
        String jsonSample = """
            {
              "name": "John",
              "email": "test@example.com",
              "createdAt": "2026-01-20T23:55:00Z"
            }
            """;

        String expectedSchema = """
            {
              "$schema": "%s",
              "type": "object",
              "properties": {
                "name": { "type": "string" },
                "email": { "type": "string", "format": "email" },
                "createdAt": { "type": "string", "format": "date-time" }
              }
            }
            """.formatted(SCHEMA_ID);

        String actualSchema = adapter.map(jsonSample);

        JSONAssert.assertEquals(expectedSchema, actualSchema, JSONCompareMode.STRICT);
    }

    @Test
    void shouldHandleEmptyObject() throws JSONException {
        String jsonSample = "{}";

        String expectedSchema = """
            {
              "$schema": "%s",
              "type": "object"
            }
            """.formatted(SCHEMA_ID);

        String actualSchema = adapter.map(jsonSample);

        JSONAssert.assertEquals(expectedSchema, actualSchema, JSONCompareMode.STRICT);
    }

    @Test
    void shouldThrowConversionExceptionOnInvalidJson() {
        String invalidJson = "{\"name\": \"John\"";

        assertThatThrownBy(() -> adapter.map(invalidJson))
                .isInstanceOf(ConversionException.class)
                .hasMessage("Failed to convert json sample to json schema")
                .hasCauseInstanceOf(com.fasterxml.jackson.core.JsonParseException.class);
    }
}
