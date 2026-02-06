package com.voleksiienko.specforgeapi.core.domain.model.spec.type;

import com.voleksiienko.specforgeapi.core.domain.exception.SpecModelValidationException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DecimalSpecTypeTest {

    @Test
    void shouldBuildValidDoubleTypeWithExplicitRange() {
        var type = DecimalSpecType.builder()
                .scale(5)
                .minimum(new BigDecimal("10.50"))
                .maximum(new BigDecimal("20.50"))
                .build();

        assertThat(type.getScale()).isEqualTo(5);
        assertThat(type.getMinimum()).isEqualTo(new BigDecimal("10.50"));
        assertThat(type.getMaximum()).isEqualTo(new BigDecimal("20.50"));
        assertThat(type.isObjectStructure()).isFalse();
    }

    @Test
    void shouldBuildWithDefaultScale() {
        var type = DecimalSpecType.builder().build();
        assertThat(type.getScale()).isEqualTo(2);
    }

    @Test
    void shouldAllowBoundaryScaleValues() {
        var typeMin = DecimalSpecType.builder().scale(0).build();
        assertThat(typeMin.getScale()).isZero();

        var typeMax = DecimalSpecType.builder().scale(100).build();
        assertThat(typeMax.getScale()).isEqualTo(100);
    }

    @Test
    void shouldThrowWhenScaleIsNegative() {
        DecimalSpecType.Builder builder = DecimalSpecType.builder().scale(-1);

        assertThatThrownBy(builder::build)
                .isInstanceOf(SpecModelValidationException.class)
                .hasMessage("Scale [-1] cannot be negative");
    }

    @Test
    void shouldThrowWhenScaleExceedsMaxLimit() {
        DecimalSpecType.Builder builder = DecimalSpecType.builder().scale(101);

        assertThatThrownBy(builder::build)
                .isInstanceOf(SpecModelValidationException.class)
                .hasMessage("Scale [101] exceeds the maximum allowed limit of [100]");
    }

    @Test
    void shouldAllowBuildingWithNoConstraints() {
        var type = DecimalSpecType.builder().build();
        assertThat(type.getMinimum()).isNull();
        assertThat(type.getMaximum()).isNull();
    }

    @Test
    void shouldAllowPartialRanges() {
        var typeMinOnly = DecimalSpecType.builder().minimum(BigDecimal.ONE).build();
        assertThat(typeMinOnly.getMinimum()).isEqualTo(BigDecimal.ONE);
        assertThat(typeMinOnly.getMaximum()).isNull();

        var typeMaxOnly = DecimalSpecType.builder().maximum(BigDecimal.TEN).build();
        assertThat(typeMaxOnly.getMinimum()).isNull();
        assertThat(typeMaxOnly.getMaximum()).isEqualTo(BigDecimal.TEN);
    }

    @Test
    void shouldAllowEqualMinimumAndMaximum() {
        var val = new BigDecimal("50.00");
        var type = DecimalSpecType.builder().minimum(val).maximum(val).build();

        assertThat(type.getMinimum()).isEqualTo(val);
        assertThat(type.getMaximum()).isEqualTo(val);
    }

    @Test
    void shouldThrowIfMinimumGreaterThanMaximum() {
        DecimalSpecType.Builder builder =
                DecimalSpecType.builder().minimum(new BigDecimal("10.1")).maximum(new BigDecimal("10.0"));

        assertThatThrownBy(builder::build)
                .isInstanceOf(SpecModelValidationException.class)
                .hasMessageContaining("Minimum [10.1] cannot be greater than Maximum [10.0]");
    }
}
