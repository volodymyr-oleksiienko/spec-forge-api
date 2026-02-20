package com.voleksiienko.specforgeapi.core.domain.model.ts;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.voleksiienko.specforgeapi.core.domain.exception.TsModelValidationException;
import java.util.List;
import org.junit.jupiter.api.Test;

class TsInterfaceTest {

    private final TsTypeReference stringType =
            TsTypeReference.builder().typeName("string").build();
    private final TsField field =
            TsField.builder().name("prop").type(stringType).build();

    @Test
    void shouldBuildValidTsInterface() {
        TsInterface tsInterface =
                TsInterface.builder().name("MyInterface").fields(List.of(field)).build();

        assertThat(tsInterface.getName()).isEqualTo("MyInterface");
        assertThat(tsInterface.getFields()).hasSize(1);
        assertThat(tsInterface.getFields().getFirst()).isEqualTo(field);
    }

    @Test
    void shouldThrowWhenNameIsNull() {
        TsInterface.Builder builder = TsInterface.builder().name(null).fields(List.of(field));

        assertThatThrownBy(builder::build).isInstanceOf(TsModelValidationException.class);
    }

    @Test
    void shouldThrowWhenNameIsBlank() {
        TsInterface.Builder builder = TsInterface.builder().name("   ").fields(List.of(field));

        assertThatThrownBy(builder::build).isInstanceOf(TsModelValidationException.class);
    }

    @Test
    void shouldThrowWhenFieldsNull() {
        TsInterface.Builder builder = TsInterface.builder().name("MyInterface");

        assertThatThrownBy(builder::build).isInstanceOf(TsModelValidationException.class);
    }

    @Test
    void shouldThrowWhenFieldsEmpty() {
        TsInterface.Builder builder = TsInterface.builder().name("MyInterface").fields(List.of());

        assertThatThrownBy(builder::build).isInstanceOf(TsModelValidationException.class);
    }
}
