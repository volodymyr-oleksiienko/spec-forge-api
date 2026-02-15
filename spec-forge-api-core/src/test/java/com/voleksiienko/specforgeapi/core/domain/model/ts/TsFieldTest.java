package com.voleksiienko.specforgeapi.core.domain.model.ts;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.voleksiienko.specforgeapi.core.domain.exception.TsModelValidationException;
import org.junit.jupiter.api.Test;

class TsFieldTest {

    private final TsTypeReference type =
            TsTypeReference.builder().typeName("string").build();

    @Test
    void shouldBuildValidRequiredTsField() {
        TsField field = TsField.builder().name("username").type(type).build();

        assertThat(field.getName()).isEqualTo("username");
        assertThat(field.getType()).isEqualTo(type);
        assertThat(field.isOptional()).isFalse();
    }

    @Test
    void shouldBuildValidOptionalTsField() {
        TsField field =
                TsField.builder().name("email").type(type).optional(true).build();

        assertThat(field.getName()).isEqualTo("email");
        assertThat(field.isOptional()).isTrue();
    }

    @Test
    void shouldThrowWhenNameIsBlank() {
        TsField.Builder builder = TsField.builder().name("").type(type);

        assertThatThrownBy(builder::build).isInstanceOf(TsModelValidationException.class);
    }

    @Test
    void shouldThrowWhenTypeIsNull() {
        TsField.Builder builder = TsField.builder().name("validName");

        assertThatThrownBy(builder::build).isInstanceOf(TsModelValidationException.class);
    }
}
