package com.voleksiienko.specforgeapi.core.application.port.in.artifact.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.voleksiienko.specforgeapi.core.domain.exception.SpecModelValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

class GenerateFromJsonSchemaCommandTest {

    @Test
    void shouldCreateCommandWithValidSchema() {
        var schema = "{\"type\": \"object\"}";
        assertThat(new GenerateFromJsonSchemaCommand(schema).jsonSchema()).isEqualTo(schema);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", "   "})
    void shouldThrowWhenJsonSchemaIsInvalid(String invalidSchema) {
        assertThatThrownBy(() -> new GenerateFromJsonSchemaCommand(invalidSchema))
                .isInstanceOf(SpecModelValidationException.class)
                .hasMessage("jsonSchema cannot be blank");
    }
}
