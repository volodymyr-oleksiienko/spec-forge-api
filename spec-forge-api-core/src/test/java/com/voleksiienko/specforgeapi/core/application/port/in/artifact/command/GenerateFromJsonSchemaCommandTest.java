package com.voleksiienko.specforgeapi.core.application.port.in.artifact.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

import com.voleksiienko.specforgeapi.core.domain.exception.SpecModelValidationException;
import com.voleksiienko.specforgeapi.core.domain.model.config.JavaConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

class GenerateFromJsonSchemaCommandTest {

    @Test
    void shouldCreateCommandWithValidSchema() {
        var schema = "{\"type\": \"object\"}";
        JavaConfig config = mock(JavaConfig.class);

        GenerateFromJsonSchemaCommand command = new GenerateFromJsonSchemaCommand(schema, config);
        assertThat(command.jsonSchema()).isEqualTo(schema);
        assertThat(command.config()).isEqualTo(config);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", "   "})
    void shouldThrowWhenJsonSchemaIsInvalid(String invalidSchema) {
        JavaConfig config = mock(JavaConfig.class);

        assertThatThrownBy(() -> new GenerateFromJsonSchemaCommand(invalidSchema, config))
                .isInstanceOf(SpecModelValidationException.class)
                .hasMessage("jsonSchema cannot be blank");
    }
}
