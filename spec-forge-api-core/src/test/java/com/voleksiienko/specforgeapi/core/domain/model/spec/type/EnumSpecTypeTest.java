package com.voleksiienko.specforgeapi.core.domain.model.spec.type;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.voleksiienko.specforgeapi.core.domain.exception.SpecModelValidationException;
import java.util.Set;
import org.junit.jupiter.api.Test;

class EnumSpecTypeTest {

    @Test
    void shouldBuildValidEnum() {
        var values = Set.of("A", "B", "C");
        var type = EnumSpecType.builder().values(values).build();
        assertThat(type.getValues()).containsExactlyInAnyOrder("A", "B", "C");
        assertThat(type.isObjectStructure()).isFalse();
        assertThat(type.getExamples()).hasSize(2).isSubsetOf(values);
    }

    @Test
    void shouldThrowIfValuesAreEmpty() {
        EnumSpecType.Builder builder = EnumSpecType.builder().values(Set.of());

        assertThatThrownBy(builder::build)
                .isInstanceOf(SpecModelValidationException.class)
                .hasMessageContaining("EnumNodeType must have at least one value");
    }
}
