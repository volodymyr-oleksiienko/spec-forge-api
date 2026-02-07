package com.voleksiienko.specforgeapi.core.domain.model.spec.type;

public sealed interface SpecType permits PrimitiveSpecType, ObjectSpecType, ListSpecType, MapSpecType {

    boolean isObjectStructure();
}
