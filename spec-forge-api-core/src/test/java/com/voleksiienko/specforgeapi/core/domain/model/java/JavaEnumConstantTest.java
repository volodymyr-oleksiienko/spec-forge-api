package com.voleksiienko.specforgeapi.core.domain.model.java;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.voleksiienko.specforgeapi.core.domain.exception.JavaModelValidationException;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

class JavaEnumConstantTest {

    @Test
    void shouldBuildConstant() {
        var constWithArgs = JavaEnumConstant.builder()
                .name("ERROR")
                .arguments(List.of("\"500\"", "\"Internal Error\""))
                .build();

        assertThat(constWithArgs.getName()).isEqualTo("ERROR");
        assertThat(constWithArgs.getArguments()).hasSize(2).containsExactly("\"500\"", "\"Internal Error\"");
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", "   "})
    void shouldThrowWhenNameBlank(String invalidName) {
        var builder = JavaEnumConstant.builder().name(invalidName);

        assertThatThrownBy(builder::build)
                .isInstanceOf(JavaModelValidationException.class)
                .hasMessage("Enum constant name is required");
    }
}
