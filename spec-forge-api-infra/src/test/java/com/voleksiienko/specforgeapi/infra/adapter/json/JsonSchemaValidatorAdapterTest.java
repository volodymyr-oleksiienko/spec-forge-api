package com.voleksiienko.specforgeapi.infra.adapter.json;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.voleksiienko.specforgeapi.core.application.exception.ConversionException;
import com.voleksiienko.specforgeapi.core.domain.model.error.JsonMappingErrorCode;
import com.voleksiienko.specforgeapi.infra.adapter.out.json.JsonSchemaValidatorAdapter;
import org.junit.jupiter.api.Test;

class JsonSchemaValidatorAdapterTest {

    private final JsonSchemaValidatorAdapter adapter = new JsonSchemaValidatorAdapter();

    @Test
    void shouldPassWhenJsonSchemaIsValid() {
        String validSchema = """
            {
                "$schema": "https://json-schema.org/draft/2020-12/schema",
                "type": "object",
                "properties": {
                    "id": { "type": "integer" }
                }
            }
            """;

        assertThatCode(() -> adapter.validate(validSchema)).doesNotThrowAnyException();
    }

    @Test
    void shouldThrowConversionExceptionWhenJsonSchemaIsInvalid() {
        String invalidSchema = """
            {
                "$schema": "https://json-schema.org/draft/2020-12/schema",
                "type": 123
            }
            """;

        assertThatThrownBy(() -> adapter.validate(invalidSchema))
                .isInstanceOf(ConversionException.class)
                .hasMessage("Json schema is invalid")
                .extracting("errorCode")
                .isEqualTo(JsonMappingErrorCode.JSON_SCHEMA_VALIDATION_FAILED);
    }

    @Test
    void shouldThrowConversionExceptionWhenJsonIsMalformed() {
        String malformedJson = "{ \"type\": \"object\" ";

        assertThatThrownBy(() -> adapter.validate(malformedJson))
                .isInstanceOf(ConversionException.class)
                .hasMessage("Json schema contains syntax errors")
                .extracting("errorCode")
                .isEqualTo(JsonMappingErrorCode.JSON_SCHEMA_VALIDATION_FAILED);
    }
}
