package com.voleksiienko.specforgeapi.core.domain.model.spec.type;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class BooleanSpecTypeTest {

    @Test
    void shouldHaveFalseObjectStructureFlag() {
        var type = BooleanSpecType.builder().build();
        assertThat(type.isObjectStructure()).isFalse();
    }
}
