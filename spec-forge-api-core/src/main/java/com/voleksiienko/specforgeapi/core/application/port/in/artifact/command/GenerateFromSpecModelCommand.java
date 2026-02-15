package com.voleksiienko.specforgeapi.core.application.port.in.artifact.command;

import com.voleksiienko.specforgeapi.core.domain.exception.SpecModelValidationException;
import com.voleksiienko.specforgeapi.core.domain.model.config.GenerationConfig;
import com.voleksiienko.specforgeapi.core.domain.model.spec.SpecModel;
import java.util.Objects;

public record GenerateFromSpecModelCommand(SpecModel specModel, GenerationConfig config) {

    public GenerateFromSpecModelCommand {
        if (Objects.isNull(specModel)) {
            throw new SpecModelValidationException("specModel cannot be null");
        }
    }
}
