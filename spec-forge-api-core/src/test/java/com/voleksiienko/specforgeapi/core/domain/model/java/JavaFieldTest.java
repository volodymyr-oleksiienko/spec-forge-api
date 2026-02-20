package com.voleksiienko.specforgeapi.core.domain.model.java;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

import com.voleksiienko.specforgeapi.core.domain.exception.JavaModelValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

class JavaFieldTest {

    private final TypeReference mockType = mock(TypeReference.class);

    @Test
    void shouldBuildValidField() {
        var field = JavaField.builder().name("id").type(mockType).build();

        assertThat(field.getName()).isEqualTo("id");
        assertThat(field.getType()).isEqualTo(mockType);
        assertThat(field.getAnnotations()).isNull();
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", "   "})
    void shouldThrowWhenNameIsBlank(String invalidName) {
        var builder = JavaField.builder().type(mockType).name(invalidName);

        assertThatThrownBy(builder::build)
                .isInstanceOf(JavaModelValidationException.class)
                .hasMessage("Field name cannot be blank");
    }

    @Test
    void shouldThrowWhenTypeIsNull() {
        var builder = JavaField.builder().name("field");

        assertThatThrownBy(builder::build)
                .isInstanceOf(JavaModelValidationException.class)
                .hasMessage("Field type cannot be null");
    }
}
