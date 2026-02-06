package com.voleksiienko.specforgeapi.core.domain.model.spec.type;

import com.voleksiienko.specforgeapi.core.domain.exception.SpecModelValidationException;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EnumSpecTypeTest {

    @Test
    void shouldBuildValidEnum() {
        var type = EnumSpecType.builder().values(Set.of("A", "B")).build();
        assertThat(type.getValues()).containsExactlyInAnyOrder("A", "B");
        assertThat(type.isObjectStructure()).isFalse();
    }

    @Test
    void shouldThrowIfValuesAreEmpty() {
        EnumSpecType.Builder builder = EnumSpecType.builder().values(Set.of());

        assertThatThrownBy(builder::build)
                .isInstanceOf(SpecModelValidationException.class)
                .hasMessageContaining("EnumNodeType must have at least one value");
    }
}
