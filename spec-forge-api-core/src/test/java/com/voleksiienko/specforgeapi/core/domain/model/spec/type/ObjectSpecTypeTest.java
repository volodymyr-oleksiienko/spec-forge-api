package com.voleksiienko.specforgeapi.core.domain.model.spec.type;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class ObjectSpecTypeTest {

    @Test
    void shouldHaveTrueObjectStructureFlag() {
        var type = new ObjectSpecType();
        assertThat(type.isObjectStructure()).isTrue();
    }
}
