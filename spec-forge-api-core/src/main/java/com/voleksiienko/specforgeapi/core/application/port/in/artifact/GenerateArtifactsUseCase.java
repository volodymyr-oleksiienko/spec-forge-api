package com.voleksiienko.specforgeapi.core.application.port.in.artifact;

import com.voleksiienko.specforgeapi.core.application.port.in.artifact.command.GenerateFromJsonSampleCommand;
import com.voleksiienko.specforgeapi.core.application.port.in.artifact.command.GenerateFromJsonSchemaCommand;
import com.voleksiienko.specforgeapi.core.application.port.in.artifact.command.GenerateFromSpecModelCommand;
import com.voleksiienko.specforgeapi.core.application.port.in.artifact.result.ArtifactsResult;

public interface GenerateArtifactsUseCase {

    ArtifactsResult generateFromJsonSchema(GenerateFromJsonSchemaCommand command);

    ArtifactsResult generateFromJsonSample(GenerateFromJsonSampleCommand command);

    ArtifactsResult generateFromSpecModel(GenerateFromSpecModelCommand command);
}
