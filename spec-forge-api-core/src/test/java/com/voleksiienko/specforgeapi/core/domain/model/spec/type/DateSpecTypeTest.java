package com.voleksiienko.specforgeapi.core.domain.model.spec.type;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.voleksiienko.specforgeapi.core.domain.exception.SpecModelValidationException;
import org.junit.jupiter.api.Test;

class DateSpecTypeTest {

    public static final String DATE_FORMAT = "yyyy-MM-dd";

    @Test
    void shouldBuildValidDateType() {
        var type = DateSpecType.builder().format(DATE_FORMAT).build();
        assertThat(type.getFormat()).isEqualTo(DATE_FORMAT);
        assertThat(type.isObjectStructure()).isFalse();
        assertThat(type.getExamples()).hasSize(1).first().asString().matches("^\\d{4}-\\d{2}-\\d{2}$");
    }

    @Test
    void shouldThrowIfFormatIsEmpty() {
        DateSpecType.Builder builder = DateSpecType.builder();

        assertThatThrownBy(builder::build)
                .isInstanceOf(SpecModelValidationException.class)
                .hasMessage("Date format cannot be empty");
    }

    @Test
    void shouldThrowOnInvalidDatePattern() {
        DateSpecType.Builder builder = DateSpecType.builder().format("invalid-fmt");

        assertThatThrownBy(builder::build)
                .isInstanceOf(SpecModelValidationException.class)
                .hasMessageContaining("Invalid Date format pattern: invalid-fmt");
    }
}
