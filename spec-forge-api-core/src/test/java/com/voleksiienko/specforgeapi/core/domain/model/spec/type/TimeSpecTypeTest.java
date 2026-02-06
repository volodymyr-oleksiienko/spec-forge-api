package com.voleksiienko.specforgeapi.core.domain.model.spec.type;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.voleksiienko.specforgeapi.core.domain.exception.SpecModelValidationException;
import org.junit.jupiter.api.Test;

class TimeSpecTypeTest {

    @Test
    void shouldBuildValidTimeType() {
        var type = TimeSpecType.builder().format("HH:mm:ss").build();
        assertThat(type.getFormat()).isEqualTo("HH:mm:ss");
        assertThat(type.isObjectStructure()).isFalse();
    }

    @Test
    void shouldThrowIfFormatIsEmpty() {
        TimeSpecType.Builder builder = TimeSpecType.builder();

        assertThatThrownBy(builder::build)
                .isInstanceOf(SpecModelValidationException.class)
                .hasMessage("Time format cannot be empty");
    }

    @Test
    void shouldThrowOnInvalidTimePattern() {
        TimeSpecType.Builder builder = TimeSpecType.builder().format("invalid-fmt");

        assertThatThrownBy(builder::build)
                .isInstanceOf(SpecModelValidationException.class)
                .hasMessageContaining("Invalid Time format pattern: [invalid-fmt]");
    }
}
