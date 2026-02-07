package com.voleksiienko.specforgeapi.core.domain.model.spec.type;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.voleksiienko.specforgeapi.core.domain.exception.SpecModelValidationException;
import org.junit.jupiter.api.Test;

class TimeSpecTypeTest {

    private static final String TIME_FORMAT = "HH:mm";
    private static final String TIME_FORMAT_WITH_OFFSET = "HH:mm:ssXXX";

    @Test
    void shouldBuildValidTimeType() {
        var type = TimeSpecType.builder().format(TIME_FORMAT).build();

        assertThat(type.getFormat()).isEqualTo(TIME_FORMAT);
        assertThat(type.isObjectStructure()).isFalse();
        assertThat(type.getExamples()).hasSize(1).first().asString().matches("^\\d{2}:\\d{2}$");
    }

    @Test
    void shouldSupportIsoFormatWithOffset() {
        var type = TimeSpecType.builder().format(TIME_FORMAT_WITH_OFFSET).build();

        assertThat(type.getFormat()).isEqualTo(TIME_FORMAT_WITH_OFFSET);
        assertThat(type.getExamples())
                .hasSize(1)
                .first()
                .asString()
                .matches("^\\d{2}:\\d{2}:\\d{2}(Z|[+-]\\d{2}:\\d{2})$");
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
                .hasMessageContaining("Invalid Time format syntax: [invalid-fmt]");
    }
}
