package com.voleksiienko.specforgeapi.core.domain.model.java;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.voleksiienko.specforgeapi.core.domain.exception.JavaModelValidationException;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

class TypeReferenceTest {

    @Test
    void shouldBuildObjectType() {
        var type = TypeReference.builder()
                .packageName("java.lang")
                .simpleName("String")
                .build();

        assertThat(type.getPackageName()).isEqualTo("java.lang");
        assertThat(type.getSimpleName()).isEqualTo("String");
        assertThat(type.isPrimitive()).isFalse();
    }

    @Test
    void shouldBuildPrimitiveType() {
        var type = TypeReference.builder().simpleName("int").primitive(true).build();

        assertThat(type.getSimpleName()).isEqualTo("int");
        assertThat(type.getPackageName()).isNull();
        assertThat(type.isPrimitive()).isTrue();
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", "   "})
    void shouldThrowWhenSimpleNameInvalid(String invalidName) {
        var builder = TypeReference.builder().packageName("pkg").simpleName(invalidName);

        assertThatThrownBy(builder::build)
                .isInstanceOf(JavaModelValidationException.class)
                .hasMessageContaining("simpleName cannot be blank");
    }

    @Test
    void shouldThrowWhenPrimitiveHasPackage() {
        var builder = TypeReference.builder().simpleName("int").primitive(true).packageName("java.lang");

        assertThatThrownBy(builder::build)
                .isInstanceOf(JavaModelValidationException.class)
                .hasMessage("Primitive types must not have a package name");
    }

    @Test
    void shouldVerifyEquality() {
        var type1 =
                TypeReference.builder().packageName("pkg").simpleName("Name").build();
        var type2 =
                TypeReference.builder().packageName("pkg").simpleName("Name").build();
        var typeDiff =
                TypeReference.builder().packageName("other").simpleName("Name").build();

        assertThat(type1)
                .isEqualTo(type2)
                .hasSameHashCodeAs(type2)
                .isNotEqualTo(typeDiff)
                .isNotEqualTo(null);
    }

    @Test
    void shouldVerifyGenericsEquality() {
        var inner1 = TypeReference.builder()
                .packageName("java.lang")
                .simpleName("String")
                .build();
        var inner2 = TypeReference.builder()
                .packageName("java.lang")
                .simpleName("String")
                .build();

        var list1 = TypeReference.builder()
                .packageName("java.util")
                .simpleName("List")
                .genericArguments(List.of(inner1))
                .build();
        var list2 = TypeReference.builder()
                .packageName("java.util")
                .simpleName("List")
                .genericArguments(List.of(inner2))
                .build();

        assertThat(list1).isEqualTo(list2);
    }
}
