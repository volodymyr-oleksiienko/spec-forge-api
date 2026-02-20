package com.voleksiienko.specforgeapi.core.domain.model.spec.type;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.voleksiienko.specforgeapi.core.domain.exception.SpecModelValidationException;
import java.util.List;
import org.junit.jupiter.api.Test;

class StringSpecTypeTest {

    @Test
    void shouldBuildValidStringWithRegex() {
        var type = StringSpecType.builder()
                .minLength(1)
                .maxLength(10)
                .pattern("^[a-z]+$")
                .examples(List.of("word"))
                .build();

        assertThat(type.getPattern()).isEqualTo("^[a-z]+$");
        assertThat(type.getMinLength()).isEqualTo(1);
        assertThat(type.getMaxLength()).isEqualTo(10);
        assertThat(type.isObjectStructure()).isFalse();
        assertThat(type.getExamples()).containsExactlyInAnyOrder("word");
    }

    @Test
    void shouldThrowWhenExampleIsTooShort() {
        StringSpecType.Builder builder = StringSpecType.builder().minLength(5).examples(List.of("Hi"));

        assertThatThrownBy(builder::build)
                .isInstanceOf(SpecModelValidationException.class)
                .hasMessageContaining("Example [Hi] length [2] is shorter than minLength [5]");
    }

    @Test
    void shouldThrowWhenExampleIsTooLong() {
        StringSpecType.Builder builder = StringSpecType.builder().maxLength(3).examples(List.of("Hello"));

        assertThatThrownBy(builder::build)
                .isInstanceOf(SpecModelValidationException.class)
                .hasMessageContaining("Example [Hello] length [5] is longer than maxLength [3]");
    }

    @Test
    void shouldThrowWhenExampleDoesNotMatchPattern() {
        StringSpecType.Builder builder =
                StringSpecType.builder().pattern("^[0-9]+$").examples(List.of("123a"));

        assertThatThrownBy(builder::build)
                .isInstanceOf(SpecModelValidationException.class)
                .hasMessageContaining("Example [123a] does not match pattern [^[0-9]+$]");
    }

    @Test
    void shouldThrowWhenExampleIsNotValidUUID() {
        StringSpecType.Builder builder = StringSpecType.builder()
                .format(StringSpecType.StringTypeFormat.UUID)
                .examples(List.of("not-a-uuid"));

        assertThatThrownBy(builder::build)
                .isInstanceOf(SpecModelValidationException.class)
                .hasMessageContaining("Example [not-a-uuid] is not a valid UUID");
    }

    @Test
    void shouldThrowWhenExampleIsNotValidEmail() {
        StringSpecType.Builder builder = StringSpecType.builder()
                .format(StringSpecType.StringTypeFormat.EMAIL)
                .examples(List.of("invalid-email.com", "user@"));

        assertThatThrownBy(builder::build)
                .isInstanceOf(SpecModelValidationException.class)
                .hasMessageContaining("Example [invalid-email.com] is not a valid EMAIL");
    }

    @Test
    void shouldBuildValidUUIDExample() {
        var uuid = "550e8400-e29b-41d4-a716-446655440000";
        var type = StringSpecType.builder()
                .format(StringSpecType.StringTypeFormat.UUID)
                .examples(List.of(uuid))
                .build();

        assertThat(type.getExamples()).containsExactly(uuid);
    }

    @Test
    void shouldBuildValidEmailExample() {
        var email = "user@example.com";
        var type = StringSpecType.builder()
                .format(StringSpecType.StringTypeFormat.EMAIL)
                .examples(List.of(email))
                .build();

        assertThat(type.getExamples()).containsExactly(email);
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
        StringSpecType.Builder minLengthBuilder = StringSpecType.builder().minLength(-1);

        assertThatThrownBy(minLengthBuilder::build)
                .isInstanceOf(SpecModelValidationException.class)
                .hasMessageContaining("MinLength [-1] cannot be negative");

        StringSpecType.Builder maxLengthBuilder = StringSpecType.builder().maxLength(-1);

        assertThatThrownBy(maxLengthBuilder::build)
                .isInstanceOf(SpecModelValidationException.class)
                .hasMessageContaining("MaxLength [-1] cannot be negative");
    }

    @Test
    void shouldThrowIfMinLengthGreaterThanMaxLength() {
        StringSpecType.Builder builder = StringSpecType.builder().minLength(10).maxLength(5);

        assertThatThrownBy(builder::build)
                .isInstanceOf(SpecModelValidationException.class)
                .hasMessageContaining("cannot be greater than MaxLength");
    }

    @Test
    void shouldThrowIfBothPatternAndFormatArePresent() {
        StringSpecType.Builder builder =
                StringSpecType.builder().pattern(".*").format(StringSpecType.StringTypeFormat.UUID);

        assertThatThrownBy(builder::build)
                .isInstanceOf(SpecModelValidationException.class)
                .hasMessageContaining("Cannot use both 'pattern' and 'format' together");
    }

    @Test
    void shouldThrowIfInvalidRegex() {
        StringSpecType.Builder builder = StringSpecType.builder().pattern("[invalid");

        assertThatThrownBy(builder::build)
                .isInstanceOf(SpecModelValidationException.class)
                .hasMessageContaining("Invalid Regex Pattern provided: [[invalid]");
    }
}
