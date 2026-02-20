package com.voleksiienko.specforgeapi.core.domain.model.ts;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.voleksiienko.specforgeapi.core.domain.exception.TsModelValidationException;
import java.util.List;
import org.junit.jupiter.api.Test;

class TsEnumTest {

    private final TsEnumConstant constant =
            TsEnumConstant.builder().key("A").value("a").build();

    @Test
    void shouldBuildValidTsEnum() {
        TsEnum tsEnum =
                TsEnum.builder().name("MyEnum").constants(List.of(constant)).build();

        assertThat(tsEnum.getName()).isEqualTo("MyEnum");
        assertThat(tsEnum.getConstants()).hasSize(1);
        assertThat(tsEnum.getConstants().getFirst()).isEqualTo(constant);
    }

    @Test
    void shouldThrowWhenNameInvalid() {
        TsEnum.Builder builder = TsEnum.builder().name("").constants(List.of(constant));

        assertThatThrownBy(builder::build).isInstanceOf(TsModelValidationException.class);
    }

    @Test
    void shouldThrowWhenConstantsEmpty() {
        TsEnum.Builder builder = TsEnum.builder().name("MyEnum").constants(List.of());

        assertThatThrownBy(builder::build).isInstanceOf(TsModelValidationException.class);
    }
}
