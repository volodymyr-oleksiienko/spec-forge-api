package com.voleksiienko.specforgeapi.core.domain.model.ts;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.voleksiienko.specforgeapi.core.domain.exception.TsModelValidationException;
import org.junit.jupiter.api.Test;

class TsEnumConstantTest {

    @Test
    void shouldBuildValidTsEnumConstant() {
        TsEnumConstant constant =
                TsEnumConstant.builder().key("KEY").value("value").build();

        assertThat(constant.getKey()).isEqualTo("KEY");
        assertThat(constant.getValue()).isEqualTo("value");
    }

    @Test
    void shouldThrowWhenKeyIsBlank() {
        TsEnumConstant.Builder builder = TsEnumConstant.builder().key("").value("val");

        assertThatThrownBy(builder::build).isInstanceOf(TsModelValidationException.class);
    }

    @Test
    void shouldThrowWhenValueIsBlank() {
        TsEnumConstant.Builder builder = TsEnumConstant.builder().key("Key").value("   ");

        assertThatThrownBy(builder::build).isInstanceOf(TsModelValidationException.class);
    }
}
