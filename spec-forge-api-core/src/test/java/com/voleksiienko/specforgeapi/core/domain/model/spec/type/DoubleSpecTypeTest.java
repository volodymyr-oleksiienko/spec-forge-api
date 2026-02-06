package com.voleksiienko.specforgeapi.core.domain.model.spec.type;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.voleksiienko.specforgeapi.core.domain.exception.SpecModelValidationException;
import org.junit.jupiter.api.Test;

class DoubleSpecTypeTest {

    @Test
    void shouldBuildValidDoubleTypeWithExplicitRange() {
        var type = DoubleSpecType.builder().minimum(1.5).maximum(10.5).build();

        assertThat(type.getMinimum()).isEqualTo(1.5);
        assertThat(type.getMaximum()).isEqualTo(10.5);
        assertThat(type.isObjectStructure()).isFalse();
    }

    @Test
    void shouldAllowBuildingWithNoConstraints() {
        var type = DoubleSpecType.builder().build();
        assertThat(type.getMinimum()).isNull();
        assertThat(type.getMaximum()).isNull();
    }

    @Test
    void shouldAllowPartialRanges() {
        var typeMin = DoubleSpecType.builder().minimum(5.5).build();
        assertThat(typeMin.getMinimum()).isEqualTo(5.5);
        assertThat(typeMin.getMaximum()).isNull();

        var typeMax = DoubleSpecType.builder().maximum(100.0).build();
        assertThat(typeMax.getMinimum()).isNull();
        assertThat(typeMax.getMaximum()).isEqualTo(100.0);
    }

    @Test
    void shouldAllowEqualMinimumAndMaximum() {
        var type = DoubleSpecType.builder().minimum(10.0).maximum(10.0).build();

        assertThat(type.getMinimum()).isEqualTo(10.0);
        assertThat(type.getMaximum()).isEqualTo(10.0);
    }

    @Test
    void shouldThrowIfMinimumGreaterThanMaximum() {
        assertThatThrownBy(() ->
                        DoubleSpecType.builder().minimum(10.5).maximum(10.4).build())
                .isInstanceOf(SpecModelValidationException.class)
                .hasMessageContaining("Minimum [10.5] cannot be greater than Maximum [10.4]");
    }
}
