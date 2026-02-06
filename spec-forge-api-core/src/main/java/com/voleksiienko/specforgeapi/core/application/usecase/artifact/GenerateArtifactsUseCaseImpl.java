package com.voleksiienko.specforgeapi.core.application.usecase.artifact;

import com.voleksiienko.specforgeapi.core.application.annotation.UseCase;
import com.voleksiienko.specforgeapi.core.application.port.in.artifact.GenerateArtifactsUseCase;
import com.voleksiienko.specforgeapi.core.application.port.in.artifact.command.GenerateFromJsonSampleCommand;
import com.voleksiienko.specforgeapi.core.application.port.in.artifact.command.GenerateFromJsonSchemaCommand;
import com.voleksiienko.specforgeapi.core.application.port.in.artifact.result.ArtifactsResult;
import com.voleksiienko.specforgeapi.core.application.port.out.json.JsonSampleToJsonSchemaPort;
import com.voleksiienko.specforgeapi.core.application.port.out.json.JsonSchemaToSpecModelPort;
import com.voleksiienko.specforgeapi.core.application.port.out.json.JsonSchemaValidatorPort;
import com.voleksiienko.specforgeapi.core.domain.model.conversion.ConversionResult;

@UseCase
public class GenerateArtifactsUseCaseImpl implements GenerateArtifactsUseCase {

    private final JsonSchemaValidatorPort jsonSchemaValidator;
    private final JsonSchemaToSpecModelPort schemaParser;
    private final JsonSampleToJsonSchemaPort sampleMapper;

    public GenerateArtifactsUseCaseImpl(
            JsonSchemaValidatorPort jsonSchemaValidator,
            JsonSchemaToSpecModelPort schemaParser,
            JsonSampleToJsonSchemaPort sampleMapper) {
        this.jsonSchemaValidator = jsonSchemaValidator;
        this.schemaParser = schemaParser;
        this.sampleMapper = sampleMapper;
    }

    @Override
    public ArtifactsResult generateFromJsonSchema(GenerateFromJsonSchemaCommand command) {
        jsonSchemaValidator.validate(command.jsonSchema());
        ConversionResult conversionResult = schemaParser.map(command.jsonSchema());
        return processSpecModel(conversionResult);
    }

    @Override
    public ArtifactsResult generateFromJsonSample(GenerateFromJsonSampleCommand command) {
        String inferredSchema = sampleMapper.map(command.jsonSample());
        ConversionResult conversionResult = schemaParser.map(inferredSchema);
        return processSpecModel(conversionResult);
    }

    private ArtifactsResult processSpecModel(ConversionResult conversionResult) {
        return new ArtifactsResult(conversionResult.model(), conversionResult.warnings());
    }
}
