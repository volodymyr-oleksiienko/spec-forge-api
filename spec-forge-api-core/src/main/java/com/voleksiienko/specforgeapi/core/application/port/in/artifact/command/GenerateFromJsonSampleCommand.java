package com.voleksiienko.specforgeapi.core.application.port.in.artifact.command;

import com.voleksiienko.specforgeapi.core.common.Asserts;
import com.voleksiienko.specforgeapi.core.domain.exception.SpecModelValidationException;

public record GenerateFromJsonSampleCommand(String jsonSample) {

    public GenerateFromJsonSampleCommand {
        if (Asserts.isBlank(jsonSample)) {
            throw new SpecModelValidationException("jsonSample cannot be blank");
        }
    }
}
