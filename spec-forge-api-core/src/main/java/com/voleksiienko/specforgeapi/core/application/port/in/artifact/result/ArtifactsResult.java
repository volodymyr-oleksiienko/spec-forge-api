package com.voleksiienko.specforgeapi.core.application.port.in.artifact.result;

import static com.voleksiienko.specforgeapi.core.common.Asserts.requireNotBlank;
import static java.util.Objects.requireNonNull;

import com.voleksiienko.specforgeapi.core.domain.model.conversion.Warning;
import com.voleksiienko.specforgeapi.core.domain.model.spec.SpecModel;
import java.util.List;
import java.util.Objects;

public record ArtifactsResult(
        SpecModel specModel, String jsonSample, String jsonSchema, String code, List<Warning> warnings) {

    public ArtifactsResult {
        requireNonNull(specModel, "specModel is mandatory");
        requireNotBlank(jsonSample, "jsonSample is mandatory");
        requireNotBlank(jsonSchema, "jsonSchema is mandatory");
        warnings = Objects.isNull(warnings) ? null : List.copyOf(warnings);
    }
}
