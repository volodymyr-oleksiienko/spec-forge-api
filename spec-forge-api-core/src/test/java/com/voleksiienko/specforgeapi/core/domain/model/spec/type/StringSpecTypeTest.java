package com.voleksiienko.specforgeapi.core.domain.model.spec.type;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.voleksiienko.specforgeapi.core.domain.exception.SpecModelValidationException;
import org.junit.jupiter.api.Test;

class StringSpecTypeTest {

    @Test
    void shouldBuildValidStringWithRegex() {
        var type = StringSpecType.builder()
                .minLength(1)
                .maxLength(10)
                .pattern("^[a-z]+$")
                .build();

        assertThat(type.getPattern()).isEqualTo("^[a-z]+$");
        assertThat(type.getMinLength()).isEqualTo(1);
        assertThat(type.getMaxLength()).isEqualTo(10);
        assertThat(type.isObjectStructure()).isFalse();
    }

    @Test
    void shouldBuildValidStringWithFormat() {
        var type = StringSpecType.builder()
                .format(StringSpecType.StringTypeFormat.EMAIL)
                .build();
        assertThat(type.getFormat()).isEqualTo(StringSpecType.StringTypeFormat.EMAIL);
    }

    @Test
    void shouldThrowIfNegativeLengths() {
        assertThatThrownBy(() -> StringSpecType.builder().minLength(-1).build())
                .isInstanceOf(SpecModelValidationException.class)
                .hasMessageContaining("MinLength [-1] cannot be negative");

        assertThatThrownBy(() -> StringSpecType.builder().maxLength(-1).build())
                .isInstanceOf(SpecModelValidationException.class)
                .hasMessageContaining("MaxLength [-1] cannot be negative");
    }

    @Test
    void shouldThrowIfMinLengthGreaterThanMaxLength() {
        assertThatThrownBy(() ->
                        StringSpecType.builder().minLength(10).maxLength(5).build())
                .isInstanceOf(SpecModelValidationException.class)
                .hasMessageContaining("cannot be greater than MaxLength");
    }

    @Test
    void shouldThrowIfBothPatternAndFormatArePresent() {
        assertThatThrownBy(() -> StringSpecType.builder()
                        .pattern(".*")
                        .format(StringSpecType.StringTypeFormat.UUID)
                        .build())
                .isInstanceOf(SpecModelValidationException.class)
                .hasMessageContaining("Cannot use both 'pattern' and 'format' together");
    }

    @Test
    void shouldThrowIfInvalidRegex() {
        assertThatThrownBy(() -> StringSpecType.builder().pattern("[invalid").build())
                .isInstanceOf(SpecModelValidationException.class)
                .hasMessageContaining("Invalid Regex Pattern provided: [[invalid]");
    }
}
