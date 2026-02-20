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

class GenerateFromJsonSampleCommandTest {

    @Test
    void shouldCreateCommandWithValidJson() {
        var json = "{\"key\": \"value\"}";
        var config = mock(JavaConfig.class);

        GenerateFromJsonSampleCommand command = new GenerateFromJsonSampleCommand(json, config);
        assertThat(command.jsonSample()).isEqualTo(json);
        assertThat(command.config()).isEqualTo(config);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", "   "})
    void shouldThrowWhenJsonSampleIsInvalid(String invalidJson) {
        JavaConfig config = mock(JavaConfig.class);

        assertThatThrownBy(() -> new GenerateFromJsonSampleCommand(invalidJson, config))
                .isInstanceOf(SpecModelValidationException.class)
                .hasMessage("jsonSample cannot be blank");
    }
}
