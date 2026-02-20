package com.voleksiienko.specforgeapi.core.domain.model.spec.type;

import static com.voleksiienko.specforgeapi.core.TestHelper.buildObjectSpecTypeSample;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.voleksiienko.specforgeapi.core.domain.exception.SpecModelValidationException;
import java.util.List;
import org.junit.jupiter.api.Test;

class ObjectSpecTypeTest {

    @Test
    void shouldHaveTrueObjectStructureFlag() {
        var type = buildObjectSpecTypeSample();
        assertThat(type.isObjectStructure()).isTrue();
    }

    @Test
    void shouldThrowWhenObjectTypeHasNoChildren() {
        var builder = ObjectSpecType.builder().children(List.of());

        assertThatThrownBy(builder::build)
                .isInstanceOf(SpecModelValidationException.class)
                .hasMessageContaining("Object must have children to define its structure");
    }
}
