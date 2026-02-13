package com.voleksiienko.specforgeapi.core.domain.model.java;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.voleksiienko.specforgeapi.core.domain.exception.JavaModelValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

class JavaAnnotationTest {

    @Test
    void shouldBuildValidAnnotation() {
        var annotation = JavaAnnotation.builder()
                .packageName("jakarta.persistence")
                .simpleName("Entity")
                .build();

        assertThat(annotation.getPackageName()).isEqualTo("jakarta.persistence");
        assertThat(annotation.getSimpleName()).isEqualTo("Entity");
        assertThat(annotation.getAttributes()).isNull();
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", "   "})
    void shouldThrowWhenSimpleNameIsInvalid(String invalidName) {
        var builder = JavaAnnotation.builder().packageName("com.example").simpleName(invalidName);

        assertThatThrownBy(builder::build)
                .isInstanceOf(JavaModelValidationException.class)
                .hasMessage("Annotation simpleName must be not blank");
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", "   "})
    void shouldThrowWhenPackageNameIsInvalid(String invalidPackage) {
        var builder = JavaAnnotation.builder().simpleName("ValidName").packageName(invalidPackage);

        assertThatThrownBy(builder::build)
                .isInstanceOf(JavaModelValidationException.class)
                .hasMessage("Annotation packageName must be not blank");
    }
}
