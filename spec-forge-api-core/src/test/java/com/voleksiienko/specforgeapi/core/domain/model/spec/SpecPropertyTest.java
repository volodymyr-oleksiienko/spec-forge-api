package com.voleksiienko.specforgeapi.core.domain.model.spec;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.voleksiienko.specforgeapi.core.domain.exception.SpecModelValidationException;
import com.voleksiienko.specforgeapi.core.domain.model.spec.type.BooleanSpecType;
import com.voleksiienko.specforgeapi.core.domain.model.spec.type.ObjectSpecType;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;

class SpecPropertyTest {

    @Test
    void shouldBuildValidPrimitiveProperty() {
        var prop = SpecProperty.builder()
                .name("isActive")
                .type(new BooleanSpecType())
                .required(true)
                .description("Active status")
                .examples(List.of("true", "false"))
                .deprecated(true)
                .build();

        assertThat(prop.getName()).isEqualTo("isActive");
        assertThat(prop.getType()).isInstanceOf(BooleanSpecType.class);
        assertThat(prop.isRequired()).isTrue();
        assertThat(prop.getDescription()).isEqualTo("Active status");
        assertThat(prop.getExamples()).containsExactly("true", "false");
        assertThat(prop.isDeprecated()).isTrue();
        assertThat(prop.getChildren()).isEmpty();
    }

    @Test
    void shouldBuildValidObjectPropertyWithChildren() {
        var child = SpecProperty.builder()
                .name("childId")
                .type(new BooleanSpecType())
                .build();

        var parent = SpecProperty.builder()
                .name("parentObj")
                .type(new ObjectSpecType())
                .children(List.of(child))
                .build();

        assertThat(parent.getChildren()).hasSize(1);
        assertThat(parent.getChildren().get(0).getName()).isEqualTo("childId");
    }

    @Test
    void shouldThrowWhenNameIsMissing() {
        var builder = SpecProperty.builder().type(new BooleanSpecType());

        assertThatThrownBy(builder::build)
                .isInstanceOf(SpecModelValidationException.class)
                .hasMessage("SpecNode must have name");
    }

    @Test
    void shouldThrowWhenTypeIsMissing() {
        var builder = SpecProperty.builder().name("test");

        assertThatThrownBy(builder::build)
                .isInstanceOf(SpecModelValidationException.class)
                .hasMessage("SpecNode must have type");
    }

    @Test
    void shouldThrowWhenObjectTypeHasNoChildren() {
        var builder = SpecProperty.builder()
                .name("emptyObj")
                .type(new ObjectSpecType())
                .children(List.of());

        assertThatThrownBy(builder::build)
                .isInstanceOf(SpecModelValidationException.class)
                .hasMessageContaining("must have children to define its structure");
    }

    @Test
    void shouldThrowWhenPrimitiveTypeHasChildren() {
        var child = SpecProperty.builder().name("c").type(new BooleanSpecType()).build();
        var builder =
                SpecProperty.builder().name("prim").type(new BooleanSpecType()).children(List.of(child));

        assertThatThrownBy(builder::build)
                .isInstanceOf(SpecModelValidationException.class)
                .hasMessageContaining("is primitive and cannot have children");
    }

    @Test
    void shouldThrowIfNullPropertyIsPresentInList() {
        List<SpecProperty> listWithNull = Collections.singletonList(null);
        assertThatThrownBy(() -> SpecProperty.ensurePropertiesUniqueness(listWithNull, "TestContext"))
                .isInstanceOf(SpecModelValidationException.class)
                .hasMessageContaining("cannot contain null properties");
    }

    @Test
    void shouldThrowOnDuplicateChildNames() {
        var child1 =
                SpecProperty.builder().name("dup").type(new BooleanSpecType()).build();
        var child2 =
                SpecProperty.builder().name("dup").type(new BooleanSpecType()).build();

        var builder =
                SpecProperty.builder().name("parent").type(new ObjectSpecType()).children(List.of(child1, child2));

        assertThatThrownBy(builder::build)
                .isInstanceOf(SpecModelValidationException.class)
                .hasMessageContaining("contains duplicate property name [dup]");
    }
}
