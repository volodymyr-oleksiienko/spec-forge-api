package com.voleksiienko.specforgeapi.core.domain.model.spec.type;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.voleksiienko.specforgeapi.core.domain.exception.SpecModelValidationException;
import org.junit.jupiter.api.Test;

class DateSpecTypeTest {

    @Test
    void shouldBuildValidDateType() {
        var type = DateSpecType.builder().format("yyyy-MM-dd").build();
        assertThat(type.getFormat()).isEqualTo("yyyy-MM-dd");
        assertThat(type.isObjectStructure()).isFalse();
    }

    @Test
    void shouldThrowIfFormatIsEmpty() {
        assertThatThrownBy(() -> DateSpecType.builder().build())
                .isInstanceOf(SpecModelValidationException.class)
                .hasMessage("Date format cannot be empty");
    }

    @Test
    void shouldThrowOnInvalidDatePattern() {
        assertThatThrownBy(() -> DateSpecType.builder().format("invalid-fmt").build())
                .isInstanceOf(SpecModelValidationException.class)
                .hasMessageContaining("Invalid Date format pattern: invalid-fmt");
    }
}
