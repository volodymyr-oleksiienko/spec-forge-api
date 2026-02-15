package com.voleksiienko.specforgeapi.infra.adapter.in.web.dto.request;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "language")
@JsonSubTypes({
    @JsonSubTypes.Type(value = JavaConfigDto.class, name = "JAVA"),
    @JsonSubTypes.Type(value = TypeScriptConfigDto.class, name = "TYPESCRIPT")
})
public sealed interface GenerationConfigDto permits JavaConfigDto, TypeScriptConfigDto {}
