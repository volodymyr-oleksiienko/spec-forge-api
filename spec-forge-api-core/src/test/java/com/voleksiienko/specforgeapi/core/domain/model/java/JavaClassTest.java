package com.voleksiienko.specforgeapi.core.domain.model.java;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

import com.voleksiienko.specforgeapi.core.domain.exception.JavaModelValidationException;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

class JavaClassTest {

    private final JavaField mockField = mock(JavaField.class);
    private final List<JavaField> fields = List.of(mockField);
    private final JavaAnnotation mockAnnotation = mock(JavaAnnotation.class);
    private final List<JavaAnnotation> annotations = List.of(mockAnnotation);

    @Test
    void shouldCreateClass() {
        var javaClass = JavaClass.createClass("User", null, fields);

        assertThat(javaClass.getName()).isEqualTo("User");
        assertThat(javaClass.isRecord()).isFalse();
        assertThat(javaClass.getFields()).hasSize(1);
        assertThat(javaClass.getNestedClasses()).isNull();
    }

    @Test
    void shouldCreateRecord() {
        var record = JavaClass.createRecord("UserRecord", annotations, fields);

        assertThat(record.getName()).isEqualTo("UserRecord");
        assertThat(record.isRecord()).isTrue();
    }

    @Test
    void shouldCreateWithNested() {
        var nested = mock(JavaClass.class);
        var javaClass = JavaClass.createClass("Outer", null, fields, List.of(nested));

        assertThat(javaClass.getNestedClasses()).hasSize(1).contains(nested);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", "   "})
    void shouldThrowWhenNameIsBlank(String invalidName) {
        assertThatThrownBy(() -> JavaClass.createClass(invalidName, annotations, fields))
                .isInstanceOf(JavaModelValidationException.class)
                .hasMessage("JavaType name cannot be blank");
    }

    @Test
    void shouldThrowWhenFieldsEmpty() {
        assertThatThrownBy(() -> JavaClass.createClass("User", annotations, null))
                .isInstanceOf(JavaModelValidationException.class)
                .hasMessage("Java class fields cannot be empty");

        assertThatThrownBy(() -> JavaClass.createClass("User", annotations, List.of()))
                .isInstanceOf(JavaModelValidationException.class)
                .hasMessage("Java class fields cannot be empty");
    }
}
