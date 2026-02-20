package com.voleksiienko.specforgeapi.core.application.usecase.artifact;

import static com.voleksiienko.specforgeapi.core.domain.model.error.DomainErrorCode.CODE_GENERATION_FAILED;

import com.voleksiienko.specforgeapi.core.application.annotation.UseCase;
import com.voleksiienko.specforgeapi.core.application.port.in.artifact.GenerateArtifactsUseCase;
import com.voleksiienko.specforgeapi.core.application.port.in.artifact.command.GenerateFromJsonSampleCommand;
import com.voleksiienko.specforgeapi.core.application.port.in.artifact.command.GenerateFromJsonSchemaCommand;
import com.voleksiienko.specforgeapi.core.application.port.in.artifact.command.GenerateFromSpecModelCommand;
import com.voleksiienko.specforgeapi.core.application.port.in.artifact.result.ArtifactsResult;
import com.voleksiienko.specforgeapi.core.application.port.out.java.JavaModelToJavaCodePort;
import com.voleksiienko.specforgeapi.core.application.port.out.json.*;
import com.voleksiienko.specforgeapi.core.application.port.out.ts.TsModelToTsCodePort;
import com.voleksiienko.specforgeapi.core.application.service.java.SpecModelToJavaModelMapper;
import com.voleksiienko.specforgeapi.core.application.service.ts.SpecModelToTsModelMapper;
import com.voleksiienko.specforgeapi.core.domain.model.config.GenerationConfig;
import com.voleksiienko.specforgeapi.core.domain.model.config.JavaConfig;
import com.voleksiienko.specforgeapi.core.domain.model.config.TypeScriptConfig;
import com.voleksiienko.specforgeapi.core.domain.model.conversion.ConversionResult;
import com.voleksiienko.specforgeapi.core.domain.model.conversion.Warning;
import com.voleksiienko.specforgeapi.core.domain.model.spec.SpecModel;
import java.util.ArrayList;
import java.util.List;

@UseCase
public class GenerateArtifactsUseCaseImpl implements GenerateArtifactsUseCase {

    private final JsonSchemaValidatorPort jsonSchemaValidator;
    private final JsonSchemaToSpecModelPort schemaParser;
    private final JsonSampleToJsonSchemaPort sampleMapper;
    private final SpecModelToJsonSamplePort sampleGenerator;
    private final SpecModelToJsonSchemaPort schemaGenerator;
    private final SpecModelToJavaModelMapper javaModelMapper;
    private final JavaModelToJavaCodePort javaCodeMapper;
    private final SpecModelToTsModelMapper tsModelMapper;
    private final TsModelToTsCodePort tsCodeMapper;

    public GenerateArtifactsUseCaseImpl(
            JsonSchemaValidatorPort jsonSchemaValidator,
            JsonSchemaToSpecModelPort schemaParser,
            JsonSampleToJsonSchemaPort sampleMapper,
            SpecModelToJsonSamplePort sampleGenerator,
            SpecModelToJsonSchemaPort schemaGenerator,
            SpecModelToJavaModelMapper javaModelMapper,
            JavaModelToJavaCodePort javaCodeMapper,
            SpecModelToTsModelMapper tsModelMapper,
            TsModelToTsCodePort tsCodeMapper) {
        this.jsonSchemaValidator = jsonSchemaValidator;
        this.schemaParser = schemaParser;
        this.sampleMapper = sampleMapper;
        this.sampleGenerator = sampleGenerator;
        this.schemaGenerator = schemaGenerator;
        this.javaModelMapper = javaModelMapper;
        this.javaCodeMapper = javaCodeMapper;
        this.tsModelMapper = tsModelMapper;
        this.tsCodeMapper = tsCodeMapper;
    }

    @Override
    public ArtifactsResult generateFromJsonSchema(GenerateFromJsonSchemaCommand command) {
        jsonSchemaValidator.validate(command.jsonSchema());
        ConversionResult conversionResult = schemaParser.map(command.jsonSchema());
        return processSpecModel(conversionResult.model(), conversionResult.warnings(), command.config());
    }

    @Override
    public ArtifactsResult generateFromJsonSample(GenerateFromJsonSampleCommand command) {
        String inferredSchema = sampleMapper.map(command.jsonSample());
        ConversionResult conversionResult = schemaParser.map(inferredSchema);
        return processSpecModel(conversionResult.model(), conversionResult.warnings(), command.config());
    }

    @Override
    public ArtifactsResult generateFromSpecModel(GenerateFromSpecModelCommand command) {
        return processSpecModel(command.specModel(), new ArrayList<>(), command.config());
    }

    private ArtifactsResult processSpecModel(SpecModel specModel, List<Warning> warnings, GenerationConfig config) {
        String jsonSample = sampleGenerator.map(specModel);
        String jsonSchema = schemaGenerator.map(specModel);
        String code = generateCode(specModel, warnings, config);
        return new ArtifactsResult(specModel, jsonSample, jsonSchema, code, warnings);
    }

    private String generateCode(SpecModel specModel, List<Warning> warnings, GenerationConfig config) {
        try {
            return switch (config) {
                case JavaConfig c -> javaCodeMapper.map(javaModelMapper.map(specModel, c));
                case TypeScriptConfig c -> tsCodeMapper.map(tsModelMapper.map(specModel, c));
                case null -> null;
            };
        } catch (Exception e) {
            warnings.add(new Warning(
                    "Code generation failed (%s used)"
                            .formatted(config.getClass().getName()),
                    CODE_GENERATION_FAILED));
            return null;
        }
    }
}
