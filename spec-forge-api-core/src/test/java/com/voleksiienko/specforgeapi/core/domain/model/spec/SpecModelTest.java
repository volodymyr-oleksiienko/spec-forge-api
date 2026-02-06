package com.voleksiienko.specforgeapi.core.domain.model.spec;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.voleksiienko.specforgeapi.core.domain.exception.SpecModelValidationException;
import com.voleksiienko.specforgeapi.core.domain.model.spec.type.BooleanSpecType;
import com.voleksiienko.specforgeapi.core.domain.model.spec.type.StringSpecType;
import java.util.List;
import org.junit.jupiter.api.Test;

class SpecModelTest {

    @Test
    void shouldBuildValidSpecModelWithObjectWrapper() {
        var prop = SpecProperty.builder()
                .name("field1")
                .type(new BooleanSpecType())
                .build();

        var model = SpecModel.builder()
                .wrapperType(SpecModel.WrapperType.OBJECT)
                .specProperties(List.of(prop))
                .build();

        assertThat(model.getWrapperType()).isEqualTo(SpecModel.WrapperType.OBJECT);
        assertThat(model.getProperties()).hasSize(1);
        assertThat(model.getProperties().get(0)).isEqualTo(prop);
    }

    @Test
    void shouldBuildValidSpecModelWithListWrapper() {
        var prop = SpecProperty.builder()
                .name("field1")
                .type(new BooleanSpecType())
                .build();

        var model = SpecModel.builder()
                .wrapperType(SpecModel.WrapperType.LIST)
                .specProperties(List.of(prop))
                .build();

        assertThat(model.getWrapperType()).isEqualTo(SpecModel.WrapperType.LIST);
    }

    @Test
    void shouldThrowExceptionWhenWrapperTypeIsMissing() {
        var prop = SpecProperty.builder().name("f").type(new BooleanSpecType()).build();
        var builder = SpecModel.builder().specProperties(List.of(prop));

        assertThatThrownBy(builder::build)
                .isInstanceOf(SpecModelValidationException.class)
                .hasMessage("wrapperType is required");
    }

    @Test
    void shouldThrowExceptionWhenPropertiesListIsEmpty() {
        var builder =
                SpecModel.builder().wrapperType(SpecModel.WrapperType.OBJECT).specProperties(List.of());

        assertThatThrownBy(builder::build)
                .isInstanceOf(SpecModelValidationException.class)
                .hasMessage("SpecModel must contain at least one node");
    }

    @Test
    void shouldThrowExceptionOnDuplicatePropertyNames() {
        var p1 = SpecProperty.builder()
                .name("myField")
                .type(new BooleanSpecType())
                .build();
        var p2 = SpecProperty.builder()
                .name("myField")
                .type(StringSpecType.builder().build())
                .build();

        var builder =
                SpecModel.builder().wrapperType(SpecModel.WrapperType.OBJECT).specProperties(List.of(p1, p2));

        assertThatThrownBy(builder::build)
                .isInstanceOf(SpecModelValidationException.class)
                .hasMessageContaining("contains duplicate property name [myField]");
    }
}
