package com.voleksiienko.specforgeapi.core.domain.model.spec.type;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.voleksiienko.specforgeapi.core.domain.exception.SpecModelValidationException;
import org.junit.jupiter.api.Test;

class IntegerSpecTypeTest {

    @Test
    void shouldBuildValidIntegerTypeWithExplicitRange() {
        var type = IntegerSpecType.builder().minimum(1L).maximum(10L).build();

        assertThat(type.getMinimum()).isEqualTo(1L);
        assertThat(type.getMaximum()).isEqualTo(10L);
        assertThat(type.isObjectStructure()).isFalse();
        assertThat(type.getExamples()).containsExactlyInAnyOrder("1");
    }

    @Test
    void shouldAllowBuildingWithNoConstraints() {
        var type = IntegerSpecType.builder().build();
        assertThat(type.getMinimum()).isNull();
        assertThat(type.getMaximum()).isNull();
    }

    @Test
    void shouldAllowPartialRanges() {
        var typeMin = IntegerSpecType.builder().minimum(5L).build();
        assertThat(typeMin.getMinimum()).isEqualTo(5L);
        assertThat(typeMin.getMaximum()).isNull();

        var typeMax = IntegerSpecType.builder().maximum(100L).build();
        assertThat(typeMax.getMinimum()).isNull();
        assertThat(typeMax.getMaximum()).isEqualTo(100L);
    }

    @Test
    void shouldAllowEqualMinimumAndMaximum() {
        var type = IntegerSpecType.builder().minimum(10L).maximum(10L).build();

        assertThat(type.getMinimum()).isEqualTo(10L);
        assertThat(type.getMaximum()).isEqualTo(10L);
    }

    @Test
    void shouldThrowIfMinimumGreaterThanMaximum() {
        IntegerSpecType.Builder builder = IntegerSpecType.builder().minimum(10L).maximum(5L);

        assertThatThrownBy(builder::build)
                .isInstanceOf(SpecModelValidationException.class)
                .hasMessageContaining("Minimum [10] cannot be greater than Maximum [5]");
    }
}
