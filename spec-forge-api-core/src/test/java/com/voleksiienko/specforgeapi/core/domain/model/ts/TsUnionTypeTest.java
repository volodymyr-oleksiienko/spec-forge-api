package com.voleksiienko.specforgeapi.core.domain.model.ts;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.voleksiienko.specforgeapi.core.domain.exception.TsModelValidationException;
import java.util.List;
import org.junit.jupiter.api.Test;

class TsUnionTypeTest {

    @Test
    void shouldBuildValidTsUnionType() {
        TsUnionType union = TsUnionType.builder()
                .name("Status")
                .values(List.of("'ACTIVE'", "'INACTIVE'"))
                .build();

        assertThat(union.getName()).isEqualTo("Status");
        assertThat(union.getValues()).hasSize(2);
        assertThat(union.getValues().contains("'ACTIVE'")).isTrue();
    }

    @Test
    void shouldThrowWhenNameIsBlank() {
        TsUnionType.Builder builder = TsUnionType.builder().name("   ").values(List.of("A", "B"));

        assertThatThrownBy(builder::build).isInstanceOf(TsModelValidationException.class);
    }

    @Test
    void shouldThrowWhenValuesEmpty() {
        TsUnionType.Builder builder = TsUnionType.builder().name("Status").values(List.of());

        assertThatThrownBy(builder::build).isInstanceOf(TsModelValidationException.class);
    }
}
