package com.voleksiienko.specforgeapi.core.domain.model.ts;

public sealed interface TsDeclaration permits TsInterface, TsTypeAlias, TsEnum, TsUnionType {

    String getName();
}
