package com.voleksiienko.specforgeapi.core.domain.model.config;

public sealed interface GenerationConfig permits JavaConfig, TypeScriptConfig {
    BaseConfig base();
}
