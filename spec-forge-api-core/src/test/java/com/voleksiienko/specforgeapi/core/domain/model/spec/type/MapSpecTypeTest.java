package com.voleksiienko.specforgeapi.core.domain.model.spec.type;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.voleksiienko.specforgeapi.core.domain.exception.SpecModelValidationException;
import java.util.Set;
import org.junit.jupiter.api.Test;

class MapSpecTypeTest {

    @Test
    void shouldBuildValidMap() {
        var type = MapSpecType.builder()
                .keyType(StringSpecType.builder().build())
                .valueType(IntegerSpecType.builder().build())
                .build();

        assertThat(type.getKeyType()).isInstanceOf(StringSpecType.class);
        assertThat(type.getValueType()).isInstanceOf(IntegerSpecType.class);
    }

    @Test
    void shouldReturnTrueForIsObjectStructureIfValueTypeIsObject() {
        var type = MapSpecType.builder()
                .keyType(StringSpecType.builder().build())
                .valueType(new ObjectSpecType())
                .build();
        assertThat(type.isObjectStructure()).isTrue();
    }

    @Test
    void shouldThrowIfKeyOrValueTypeIsMissing() {
        assertThatThrownBy(() -> MapSpecType.builder()
                        .keyType(StringSpecType.builder().build())
                        .build())
                .isInstanceOf(SpecModelValidationException.class)
                .hasMessageContaining("MapSpecType must specify both keyType and valueType");
    }

    @Test
    void shouldThrowIfKeyIsNotPrimitive() {
        var listType = ListSpecType.builder()
                .valueType(StringSpecType.builder().build())
                .build();

        assertThatThrownBy(() -> MapSpecType.builder()
                        .keyType(listType)
                        .valueType(StringSpecType.builder().build())
                        .build())
                .isInstanceOf(SpecModelValidationException.class)
                .hasMessageContaining(
                        "Map key must be primitive types (String, Number, Boolean, Enum), found: ListSpecType");
    }

    @Test
    void shouldAllowEnumAsKey() {
        var enumType = EnumSpecType.builder().values(Set.of("X")).build();
        var map = MapSpecType.builder()
                .keyType(enumType)
                .valueType(new BooleanSpecType())
                .build();
        assertThat(map).isNotNull();
    }

    @Test
    void shouldThrowIfNestedMaps() {
        var innerMap = MapSpecType.builder()
                .keyType(StringSpecType.builder().build())
                .valueType(new BooleanSpecType())
                .build();

        assertThatThrownBy(() -> MapSpecType.builder()
                        .keyType(StringSpecType.builder().build())
                        .valueType(innerMap)
                        .build())
                .isInstanceOf(SpecModelValidationException.class)
                .hasMessageContaining("Map cannot contain other Map as value, nesting Maps is forbidden");
    }

    @Test
    void shouldThrowIfNestedListValues() {
        var stringType = StringSpecType.builder().build();
        var level1 = ListSpecType.builder().valueType(stringType).build();
        var level2 = ListSpecType.builder().valueType(level1).build();

        assertThatThrownBy(() -> MapSpecType.builder()
                        .keyType(stringType)
                        .valueType(level2)
                        .build())
                .isInstanceOf(SpecModelValidationException.class)
                .hasMessageContaining("Map value cannot be nested Lists, 'Map<K, List<List<...>>>' is forbidden");
    }
}
