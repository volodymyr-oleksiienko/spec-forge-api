package com.voleksiienko.specforgeapi.core.application.usecase.artifact;

import com.voleksiienko.specforgeapi.core.application.annotation.UseCase;
import com.voleksiienko.specforgeapi.core.application.port.in.artifact.GenerateArtifactsUseCase;
import com.voleksiienko.specforgeapi.core.application.port.in.artifact.command.GenerateFromJsonSampleCommand;
import com.voleksiienko.specforgeapi.core.application.port.in.artifact.command.GenerateFromJsonSchemaCommand;
import com.voleksiienko.specforgeapi.core.application.port.in.artifact.result.ArtifactsResult;
import com.voleksiienko.specforgeapi.core.application.port.out.json.*;
import com.voleksiienko.specforgeapi.core.domain.model.conversion.ConversionResult;

@UseCase
public class GenerateArtifactsUseCaseImpl implements GenerateArtifactsUseCase {

    private final JsonSchemaValidatorPort jsonSchemaValidator;
    private final JsonSchemaToSpecModelPort schemaParser;
    private final JsonSampleToJsonSchemaPort sampleMapper;
    private final SpecModelToJsonSamplePort sampleGenerator;
    private final SpecModelToJsonSchemaPort schemaGenerator;

    public GenerateArtifactsUseCaseImpl(
            JsonSchemaValidatorPort jsonSchemaValidator,
            JsonSchemaToSpecModelPort schemaParser,
            JsonSampleToJsonSchemaPort sampleMapper,
            SpecModelToJsonSamplePort sampleGenerator,
            SpecModelToJsonSchemaPort schemaGenerator) {
        this.jsonSchemaValidator = jsonSchemaValidator;
        this.schemaParser = schemaParser;
        this.sampleMapper = sampleMapper;
        this.sampleGenerator = sampleGenerator;
        this.schemaGenerator = schemaGenerator;
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
        String jsonSample = sampleGenerator.map(conversionResult.model());
        String jsonSchema = schemaGenerator.map(conversionResult.model());
        return new ArtifactsResult(conversionResult.model(), jsonSample, jsonSchema, conversionResult.warnings());
    }
}
