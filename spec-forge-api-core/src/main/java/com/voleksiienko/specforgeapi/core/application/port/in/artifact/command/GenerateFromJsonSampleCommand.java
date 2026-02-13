package com.voleksiienko.specforgeapi.core.application.port.in.artifact.command;

import com.voleksiienko.specforgeapi.core.common.Asserts;
import com.voleksiienko.specforgeapi.core.domain.exception.SpecModelValidationException;
import com.voleksiienko.specforgeapi.core.domain.model.config.GenerationConfig;

public record GenerateFromJsonSampleCommand(String jsonSample, GenerationConfig config) {

    public GenerateFromJsonSampleCommand {
        if (Asserts.isBlank(jsonSample)) {
            throw new SpecModelValidationException("jsonSample cannot be blank");
        }
    }
}
