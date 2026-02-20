package com.voleksiienko.specforgeapi.core.domain.model.spec;

import static com.voleksiienko.specforgeapi.core.domain.model.spec.SpecProperty.ensurePropertiesUniqueness;
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
                .deprecated(true)
                .build();

        assertThat(prop.getName()).isEqualTo("isActive");
        assertThat(prop.getType()).isInstanceOf(BooleanSpecType.class);
        assertThat(prop.isRequired()).isTrue();
        assertThat(prop.getDescription()).isEqualTo("Active status");
        assertThat(((BooleanSpecType) prop.getType()).getExamples()).containsExactly("true", "false");
        assertThat(prop.isDeprecated()).isTrue();
    }

    @Test
    void shouldBuildValidObjectPropertyWithChildren() {
        var child = SpecProperty.builder()
                .name("childId")
                .type(new BooleanSpecType())
                .build();

        var parent = SpecProperty.builder()
                .name("parentObj")
                .type(ObjectSpecType.builder().children(List.of(child)).build())
                .build();

        var parentType = (ObjectSpecType) parent.getType();
        assertThat(parentType.getChildren()).hasSize(1);
        assertThat(parentType.getChildren().getFirst().getName()).isEqualTo("childId");
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
    void shouldThrowIfNullPropertyIsPresentInList() {
        List<SpecProperty> listWithNull = Collections.singletonList(null);

        assertThatThrownBy(() -> ensurePropertiesUniqueness(listWithNull))
                .isInstanceOf(SpecModelValidationException.class)
                .hasMessageContaining("cannot contain null properties");
    }

    @Test
    void shouldThrowOnDuplicateChildNames() {
        var child1 =
                SpecProperty.builder().name("dup").type(new BooleanSpecType()).build();
        var child2 =
                SpecProperty.builder().name("dup").type(new BooleanSpecType()).build();

        var builder = ObjectSpecType.builder().children(List.of(child1, child2));

        assertThatThrownBy(builder::build)
                .isInstanceOf(SpecModelValidationException.class)
                .hasMessageContaining("contains duplicate property name [dup]");
    }
}
