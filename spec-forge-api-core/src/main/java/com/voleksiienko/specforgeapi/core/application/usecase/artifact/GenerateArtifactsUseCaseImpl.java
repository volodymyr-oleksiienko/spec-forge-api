package com.voleksiienko.specforgeapi.core.application.usecase.artifact;

import com.voleksiienko.specforgeapi.core.application.annotation.UseCase;
import com.voleksiienko.specforgeapi.core.application.port.in.artifact.GenerateArtifactsUseCase;
import com.voleksiienko.specforgeapi.core.application.port.in.artifact.command.GenerateFromJsonSampleCommand;
import com.voleksiienko.specforgeapi.core.application.port.in.artifact.command.GenerateFromJsonSchemaCommand;
import com.voleksiienko.specforgeapi.core.application.port.in.artifact.result.ArtifactsResult;
import com.voleksiienko.specforgeapi.core.application.port.out.json.JsonSampleToJsonSchemaPort;
import com.voleksiienko.specforgeapi.core.application.port.out.json.JsonSchemaToSpecModelPort;
import com.voleksiienko.specforgeapi.core.application.port.out.json.JsonSchemaValidatorPort;
import com.voleksiienko.specforgeapi.core.application.port.out.json.SpecModelToJsonSamplePort;
import com.voleksiienko.specforgeapi.core.domain.model.conversion.ConversionResult;

@UseCase
public class GenerateArtifactsUseCaseImpl implements GenerateArtifactsUseCase {

    private final JsonSchemaValidatorPort jsonSchemaValidator;
    private final JsonSchemaToSpecModelPort schemaParser;
    private final JsonSampleToJsonSchemaPort sampleMapper;
    private final SpecModelToJsonSamplePort sampleGenerator;

    public GenerateArtifactsUseCaseImpl(
            JsonSchemaValidatorPort jsonSchemaValidator,
            JsonSchemaToSpecModelPort schemaParser,
            JsonSampleToJsonSchemaPort sampleMapper,
            SpecModelToJsonSamplePort sampleGenerator) {
        this.jsonSchemaValidator = jsonSchemaValidator;
        this.schemaParser = schemaParser;
        this.sampleMapper = sampleMapper;
        this.sampleGenerator = sampleGenerator;
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
        return new ArtifactsResult(conversionResult.model(), jsonSample, conversionResult.warnings());
    }
}
