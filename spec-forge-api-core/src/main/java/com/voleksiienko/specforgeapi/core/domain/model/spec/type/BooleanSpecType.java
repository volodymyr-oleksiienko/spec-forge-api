package com.voleksiienko.specforgeapi.core.domain.model.spec.type;

import java.util.List;

public final class BooleanSpecType extends PrimitiveSpecType {

    private static final List<String> BOOLEAN_EXAMPLES = List.of(Boolean.TRUE.toString(), Boolean.FALSE.toString());

    public BooleanSpecType() {
        super(BOOLEAN_EXAMPLES);
    }
}
