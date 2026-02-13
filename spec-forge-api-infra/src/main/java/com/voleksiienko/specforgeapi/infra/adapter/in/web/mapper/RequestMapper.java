package com.voleksiienko.specforgeapi.infra.adapter.in.web.mapper;

import com.voleksiienko.specforgeapi.core.application.port.in.artifact.command.GenerateFromJsonSampleCommand;
import com.voleksiienko.specforgeapi.core.application.port.in.artifact.command.GenerateFromJsonSchemaCommand;
import com.voleksiienko.specforgeapi.core.domain.model.config.BaseConfig;
import com.voleksiienko.specforgeapi.core.domain.model.config.GenerationConfig;
import com.voleksiienko.specforgeapi.core.domain.model.config.JavaConfig;
import com.voleksiienko.specforgeapi.infra.adapter.in.web.dto.request.BaseConfigDto;
import com.voleksiienko.specforgeapi.infra.adapter.in.web.dto.request.GenerateFromRawRequest;
import com.voleksiienko.specforgeapi.infra.adapter.in.web.dto.request.GenerationConfigDto;
import com.voleksiienko.specforgeapi.infra.adapter.in.web.dto.request.JavaConfigDto;
import org.springframework.stereotype.Component;

@Component
public class RequestMapper {

    public GenerateFromJsonSchemaCommand toGenerateFromJsonSchemaCommand(GenerateFromRawRequest request) {
        return new GenerateFromJsonSchemaCommand(request.content(), toDomain(request.generationConfig()));
    }

    public GenerateFromJsonSampleCommand toGenerateFromJsonSampleCommand(GenerateFromRawRequest request) {
        return new GenerateFromJsonSampleCommand(request.content(), toDomain(request.generationConfig()));
    }

    public GenerationConfig toDomain(GenerationConfigDto dto) {
        return switch (dto) {
            case JavaConfigDto c -> mapJavaConfig(c);
            case null -> null;
        };
    }

    private JavaConfig mapJavaConfig(JavaConfigDto dto) {
        return new JavaConfig(
                mapBaseConfig(dto.base()),
                mapStructure(dto.structure()),
                mapValidation(dto.validation()),
                mapBuilder(dto.builder()),
                mapSerialization(dto.serialization()));
    }

    private BaseConfig mapBaseConfig(BaseConfigDto request) {
        return new BaseConfig(
                new BaseConfig.Naming(request.naming().className()),
                new BaseConfig.Fields(BaseConfig.Fields.SortType.valueOf(
                        request.fields().sort().name())));
    }

    private JavaConfig.Structure mapStructure(JavaConfigDto.StructureDto dto) {
        return new JavaConfig.Structure(
                JavaConfig.Structure.Type.valueOf(dto.type().name()));
    }

    private JavaConfig.Validation mapValidation(JavaConfigDto.ValidationDto dto) {
        return new JavaConfig.Validation(dto.enabled());
    }

    private JavaConfig.Builder mapBuilder(JavaConfigDto.BuilderDto dto) {
        return new JavaConfig.Builder(dto.enabled(), dto.onlyIfMultipleFields());
    }

    private JavaConfig.Serialization mapSerialization(JavaConfigDto.SerializationDto dto) {
        return new JavaConfig.Serialization(JavaConfig.Serialization.JsonPropertyMode.valueOf(
                dto.jsonPropertyMode().name()));
    }
}
