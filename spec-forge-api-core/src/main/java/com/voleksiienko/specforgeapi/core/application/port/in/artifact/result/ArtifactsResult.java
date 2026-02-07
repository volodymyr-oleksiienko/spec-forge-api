package com.voleksiienko.specforgeapi.core.application.port.in.artifact.result;

import com.voleksiienko.specforgeapi.core.domain.model.conversion.Warning;
import com.voleksiienko.specforgeapi.core.domain.model.spec.SpecModel;
import java.util.List;

public record ArtifactsResult(SpecModel specModel, String jsonSample, List<Warning> warnings) {}
