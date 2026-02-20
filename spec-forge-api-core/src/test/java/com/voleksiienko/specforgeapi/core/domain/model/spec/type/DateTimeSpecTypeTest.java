package com.voleksiienko.specforgeapi.core.domain.model.spec.type;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.voleksiienko.specforgeapi.core.domain.exception.SpecModelValidationException;
import org.junit.jupiter.api.Test;

class DateTimeSpecTypeTest {

    private static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm";
    private static final String DATE_TIME_FORMAT_WITH_OFFSET = "yyyy-MM-dd'T'HH:mm:ssXXX";

    @Test
    void shouldBuildValidDateTimeType() {
        var type = DateTimeSpecType.builder().format(DATE_TIME_FORMAT).build();

        assertThat(type.getFormat()).isEqualTo(DATE_TIME_FORMAT);
        assertThat(type.isObjectStructure()).isFalse();
        assertThat(type.getExamples()).hasSize(1).first().asString().matches("^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}$");
    }

    @Test
    void shouldSupportIsoFormatWithOffset() {
        var type =
                DateTimeSpecType.builder().format(DATE_TIME_FORMAT_WITH_OFFSET).build();

        assertThat(type.getFormat()).isEqualTo(DATE_TIME_FORMAT_WITH_OFFSET);
        assertThat(type.getExamples())
                .hasSize(1)
                .first()
                .asString()
                .matches("^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}(Z|[+-]\\d{2}:\\d{2})$");
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
                .hasMessageContaining("Invalid DateTime format pattern syntax: [invalid-fmt]");
    }
}
