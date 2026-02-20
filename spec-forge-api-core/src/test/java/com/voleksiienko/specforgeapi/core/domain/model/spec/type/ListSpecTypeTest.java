package com.voleksiienko.specforgeapi.core.domain.model.spec.type;

import static com.voleksiienko.specforgeapi.core.TestHelper.buildObjectSpecTypeSample;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.voleksiienko.specforgeapi.core.domain.exception.SpecModelValidationException;
import org.junit.jupiter.api.Test;

class ListSpecTypeTest {

    @Test
    void shouldBuildValidListOfIntegers() {
        var type = ListSpecType.builder()
                .valueType(IntegerSpecType.builder().build())
                .minItems(0)
                .maxItems(10)
                .build();

        assertThat(type.getValueType()).isInstanceOf(IntegerSpecType.class);
        assertThat(type.getMinItems()).isZero();
        assertThat(type.getMaxItems()).isEqualTo(10);
        assertThat(type.isObjectStructure()).isFalse();
    }

    @Test
    void shouldReturnTrueForIsObjectStructureIfValueTypeIsObject() {
        var type = ListSpecType.builder().valueType(buildObjectSpecTypeSample()).build();
        assertThat(type.isObjectStructure()).isTrue();
    }

    @Test
    void shouldThrowIfNegativeMinOrMaxItems() {
        ListSpecType.Builder minItemsBuilder = ListSpecType.builder().minItems(-1);

        assertThatThrownBy(minItemsBuilder::build)
                .isInstanceOf(SpecModelValidationException.class)
                .hasMessageContaining("MinItems [-1] cannot be negative");

        ListSpecType.Builder maxItemsBuilder = ListSpecType.builder().maxItems(-1);

        assertThatThrownBy(maxItemsBuilder::build)
                .isInstanceOf(SpecModelValidationException.class)
                .hasMessageContaining("MaxItems [-1] cannot be negative");
    }

    @Test
    void shouldThrowIfMinItemsGreaterThanMaxItems() {
        ListSpecType.Builder builder = ListSpecType.builder().minItems(5).maxItems(4);

        assertThatThrownBy(builder::build)
                .isInstanceOf(SpecModelValidationException.class)
                .hasMessageContaining("MinItems [5] cannot be greater than MaxItems [4]");
    }

    @Test
    void shouldThrowIfValueTypeIsMissing() {
        ListSpecType.Builder builder = ListSpecType.builder();

        assertThatThrownBy(builder::build)
                .isInstanceOf(SpecModelValidationException.class)
                .hasMessage("ListSpecType must specify valueType");
    }

    @Test
    void shouldThrowIfListOfMaps() {
        var mapType = MapSpecType.builder()
                .keyType(StringSpecType.builder().build())
                .valueType(IntegerSpecType.builder().build())
                .build();
        ListSpecType.Builder builder = ListSpecType.builder().valueType(mapType);

        assertThatThrownBy(builder::build)
                .isInstanceOf(SpecModelValidationException.class)
                .hasMessageContaining("Lists cannot contain Maps directly, structure 'List<Map>' is forbidden");
    }

    @Test
    void shouldThrowIfNestedListOfObjects() {
        var innerList =
                ListSpecType.builder().valueType(buildObjectSpecTypeSample()).build();
        ListSpecType.Builder builder = ListSpecType.builder().valueType(innerList);

        assertThatThrownBy(builder::build)
                .isInstanceOf(SpecModelValidationException.class)
                .hasMessageContaining("Objects cannot be nested in inner lists, 'List<List<Object>>' is forbidden");
    }

    @Test
    void shouldThrowIfExcessiveNestingDepth() {
        var stringType = StringSpecType.builder().build();
        var level1 = ListSpecType.builder().valueType(stringType).build();
        var level2 = ListSpecType.builder().valueType(level1).build();
        ListSpecType.Builder builder = ListSpecType.builder().valueType(level2);

        assertThatThrownBy(builder::build)
                .isInstanceOf(SpecModelValidationException.class)
                .hasMessageContaining("Max nesting depth exceeded, 'List<List<List<...>>>' is forbidden");
    }
}
