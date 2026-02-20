package com.voleksiienko.specforgeapi.core.domain.model.ts;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.voleksiienko.specforgeapi.core.domain.exception.TsModelValidationException;
import java.util.List;
import org.junit.jupiter.api.Test;

class TsTypeAliasTest {

    private final TsField field = TsField.builder()
            .name("data")
            .type(TsTypeReference.builder().typeName("any").build())
            .build();

    @Test
    void shouldBuildValidTsTypeAlias() {
        TsTypeAlias typeAlias =
                TsTypeAlias.builder().name("MyAlias").fields(List.of(field)).build();

        assertThat(typeAlias.getName()).isEqualTo("MyAlias");
        assertThat(typeAlias.getFields()).hasSize(1);
    }

    @Test
    void shouldThrowWhenNameIsBlank() {
        TsTypeAlias.Builder builder = TsTypeAlias.builder().name("  ").fields(List.of(field));

        assertThatThrownBy(builder::build).isInstanceOf(TsModelValidationException.class);
    }

    @Test
    void shouldThrowWhenFieldsEmpty() {
        TsTypeAlias.Builder builder = TsTypeAlias.builder().name("MyAlias").fields(List.of());

        assertThatThrownBy(builder::build).isInstanceOf(TsModelValidationException.class);
    }
}
