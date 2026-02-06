package com.voleksiienko.specforgeapi.core.domain.model.spec.type;

public sealed interface SpecType
        permits BooleanSpecType,
                IntegerSpecType,
                DoubleSpecType,
                DecimalSpecType,
                StringSpecType,
                EnumSpecType,
                DateTimeSpecType,
                DateSpecType,
                TimeSpecType,
                ObjectSpecType,
                ListSpecType,
                MapSpecType {

    boolean isObjectStructure();
}
