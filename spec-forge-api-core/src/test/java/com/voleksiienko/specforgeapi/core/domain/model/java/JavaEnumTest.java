package com.voleksiienko.specforgeapi.core.domain.model.java;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.voleksiienko.specforgeapi.core.domain.exception.JavaModelValidationException;
import java.util.List;
import org.junit.jupiter.api.Test;

class JavaEnumTest {

    private final JavaEnumConstant constant =
            JavaEnumConstant.builder().name("TEST").build();
    private final List<JavaEnumConstant> constants = List.of(constant);

    @Test
    void shouldCreateEnum() {
        var javaEnum = JavaEnum.of("Status", null, null, constants);

        assertThat(javaEnum.getName()).isEqualTo("Status");
        assertThat(javaEnum.getConstants()).hasSize(1);
        assertThat(javaEnum.getFields()).isNull();
    }

    @Test
    void shouldThrowWhenNoConstants() {
        assertThatThrownBy(() -> JavaEnum.of("Status", null, null, null))
                .isInstanceOf(JavaModelValidationException.class)
                .hasMessage("JavaEnum must have at least one constant");

        assertThatThrownBy(() -> JavaEnum.of("Status", null, null, List.of()))
                .isInstanceOf(JavaModelValidationException.class)
                .hasMessage("JavaEnum must have at least one constant");
    }
}
