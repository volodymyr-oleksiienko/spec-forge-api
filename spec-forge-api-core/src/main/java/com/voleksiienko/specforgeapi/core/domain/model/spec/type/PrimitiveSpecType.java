package com.voleksiienko.specforgeapi.core.domain.model.spec.type;

import java.util.List;

public abstract sealed class PrimitiveSpecType implements SpecType
        permits BooleanSpecType,
                IntegerSpecType,
                DoubleSpecType,
                DecimalSpecType,
                StringSpecType,
                EnumSpecType,
                DateSpecType,
                DateTimeSpecType,
                TimeSpecType {

    private final List<String> examples;

    protected PrimitiveSpecType(List<String> examples) {
        this.examples = examples;
    }

    @Override
    public boolean isObjectStructure() {
        return false;
    }

    public List<String> getExamples() {
        return examples;
    }
}
