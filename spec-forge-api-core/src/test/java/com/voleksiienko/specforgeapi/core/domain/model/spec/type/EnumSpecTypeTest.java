package com.voleksiienko.specforgeapi.core.domain.model.spec.type;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.voleksiienko.specforgeapi.core.domain.exception.SpecModelValidationException;
import java.util.Set;
import org.junit.jupiter.api.Test;

class EnumSpecTypeTest {

    @Test
    void shouldBuildValidEnum() {
        var type = EnumSpecType.builder().values(Set.of("A", "B")).build();
        assertThat(type.getValues()).containsExactlyInAnyOrder("A", "B");
        assertThat(type.isObjectStructure()).isFalse();
    }

    @Test
    void shouldThrowIfValuesAreEmpty() {
        assertThatThrownBy(() -> EnumSpecType.builder().values(Set.of()).build())
                .isInstanceOf(SpecModelValidationException.class)
                .hasMessageContaining("EnumNodeType must have at least one value");
    }
}
