package com.voleksiienko.specforgeapi.core.domain.model.ts;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.voleksiienko.specforgeapi.core.domain.exception.TsModelValidationException;
import java.util.List;
import org.junit.jupiter.api.Test;

class TsTypeReferenceTest {

    @Test
    void shouldBuildSimpleType() {
        TsTypeReference type = TsTypeReference.builder().typeName("string").build();

        assertThat(type.getTypeName()).isEqualTo("string");
        assertThat(type.getGenericArguments()).isNull();
    }

    @Test
    void shouldBuildGenericType() {
        TsTypeReference stringType =
                TsTypeReference.builder().typeName("string").build();

        TsTypeReference listType = TsTypeReference.builder()
                .typeName("List")
                .genericArguments(List.of(stringType))
                .build();

        assertThat(listType.getTypeName()).isEqualTo("List");
        assertThat(listType.getGenericArguments()).hasSize(1);
        assertThat(listType.getGenericArguments().getFirst().getTypeName()).isEqualTo("string");
    }

    @Test
    void shouldThrowWhenTypeNameIsBlank() {
        TsTypeReference.Builder builder = TsTypeReference.builder().typeName("");

        assertThatThrownBy(builder::build).isInstanceOf(TsModelValidationException.class);
    }
}
