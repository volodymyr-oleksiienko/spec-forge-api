package com.voleksiienko.specforgeapi.core.domain.model.spec.type;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.voleksiienko.specforgeapi.core.domain.exception.SpecModelValidationException;
import org.junit.jupiter.api.Test;

class DateTimeSpecTypeTest {

    @Test
    void shouldBuildValidDateTimeType() {
        var type = DateTimeSpecType.builder().format("yyyy-MM-dd'T'HH:mm:ssXXX").build();
        assertThat(type.getFormat()).isEqualTo("yyyy-MM-dd'T'HH:mm:ssXXX");
        assertThat(type.isObjectStructure()).isFalse();
    }

    @Test
    void shouldThrowIfFormatIsEmpty() {
        DateTimeSpecType.Builder builder = DateTimeSpecType.builder();

        assertThatThrownBy(builder::build)
                .isInstanceOf(SpecModelValidationException.class)
                .hasMessage("DateTime format cannot be empty");
    }

    @Test
    void shouldThrowOnInvalidDateTimePattern() {
        DateTimeSpecType.Builder builder = DateTimeSpecType.builder().format("invalid-fmt");

        assertThatThrownBy(builder::build)
                .isInstanceOf(SpecModelValidationException.class)
                .hasMessageContaining("Invalid DateTime format pattern: [invalid-fmt]");
    }
}
