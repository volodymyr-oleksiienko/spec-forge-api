package com.voleksiienko.specforgeapi.core.application.port.in.artifact.command;

import com.voleksiienko.specforgeapi.core.common.Asserts;
import com.voleksiienko.specforgeapi.core.domain.exception.SpecModelValidationException;

public record GenerateFromJsonSchemaCommand(String jsonSchema) {

    public GenerateFromJsonSchemaCommand {
        if (Asserts.isBlank(jsonSchema)) {
            throw new SpecModelValidationException("jsonSchema cannot be blank");
        }
    }
}
