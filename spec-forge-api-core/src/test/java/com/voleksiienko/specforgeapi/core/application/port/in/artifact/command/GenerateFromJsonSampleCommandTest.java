package com.voleksiienko.specforgeapi.core.application.port.in.artifact.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.voleksiienko.specforgeapi.core.domain.exception.SpecModelValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

class GenerateFromJsonSampleCommandTest {

    @Test
    void shouldCreateCommandWithValidJson() {
        var json = "{\"key\": \"value\"}";
        assertThat(new GenerateFromJsonSampleCommand(json).jsonSample()).isEqualTo(json);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", "   "})
    void shouldThrowWhenJsonSampleIsInvalid(String invalidJson) {
        assertThatThrownBy(() -> new GenerateFromJsonSampleCommand(invalidJson))
                .isInstanceOf(SpecModelValidationException.class)
                .hasMessage("jsonSample cannot be blank");
    }
}
