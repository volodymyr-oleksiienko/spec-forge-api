package com.voleksiienko.specforgeapi.core;

import com.voleksiienko.specforgeapi.core.domain.model.spec.SpecProperty;
import com.voleksiienko.specforgeapi.core.domain.model.spec.type.IntegerSpecType;
import com.voleksiienko.specforgeapi.core.domain.model.spec.type.ObjectSpecType;
import java.util.List;

public class TestHelper {

    private TestHelper() {}

    public static ObjectSpecType buildObjectSpecTypeSample() {
        return ObjectSpecType.builder()
                .children(List.of(SpecProperty.builder()
                        .name("id")
                        .type(IntegerSpecType.builder().build())
                        .build()))
                .build();
    }
}
