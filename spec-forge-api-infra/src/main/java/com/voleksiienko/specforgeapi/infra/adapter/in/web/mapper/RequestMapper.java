package com.voleksiienko.specforgeapi.infra.adapter.in.web.mapper;

import com.voleksiienko.specforgeapi.core.application.port.in.artifact.command.GenerateFromJsonSampleCommand;
import com.voleksiienko.specforgeapi.core.application.port.in.artifact.command.GenerateFromJsonSchemaCommand;
import com.voleksiienko.specforgeapi.core.application.port.in.artifact.command.GenerateFromSpecModelCommand;
import com.voleksiienko.specforgeapi.core.domain.model.config.BaseConfig;
import com.voleksiienko.specforgeapi.core.domain.model.config.GenerationConfig;
import com.voleksiienko.specforgeapi.core.domain.model.config.JavaConfig;
import com.voleksiienko.specforgeapi.core.domain.model.config.TypeScriptConfig;
import com.voleksiienko.specforgeapi.core.domain.model.spec.SpecModel;
import com.voleksiienko.specforgeapi.core.domain.model.spec.SpecProperty;
import com.voleksiienko.specforgeapi.core.domain.model.spec.type.*;
import com.voleksiienko.specforgeapi.infra.adapter.in.web.dto.SpecModelDto;
import com.voleksiienko.specforgeapi.infra.adapter.in.web.dto.request.*;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class RequestMapper {

    public GenerateFromJsonSchemaCommand toGenerateFromJsonSchemaCommand(GenerateFromRawRequest request) {
        return new GenerateFromJsonSchemaCommand(request.content(), toDomain(request.generationConfig()));
    }

    public GenerateFromJsonSampleCommand toGenerateFromJsonSampleCommand(GenerateFromRawRequest request) {
        return new GenerateFromJsonSampleCommand(request.content(), toDomain(request.generationConfig()));
    }

    public GenerateFromSpecModelCommand toGenerateFromSpecModelCommand(GenerateFromSpecModelRequest request) {
        return new GenerateFromSpecModelCommand(toDomain(request.specModel()), toDomain(request.generationConfig()));
    }

    private GenerationConfig toDomain(GenerationConfigDto dto) {
        return switch (dto) {
            case JavaConfigDto c -> mapJavaConfig(c);
            case TypeScriptConfigDto c -> mapTypeScriptConfig(c);
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

    public TypeScriptConfig mapTypeScriptConfig(TypeScriptConfigDto dto) {
        return new TypeScriptConfig(mapBaseConfig(dto.base()), mapStructure(dto.structure()), mapEnums(dto.enums()));
    }

    private TypeScriptConfig.Structure mapStructure(TypeScriptConfigDto.StructureDto dto) {
        return new TypeScriptConfig.Structure(
                TypeScriptConfig.Structure.DeclarationStyle.valueOf(dto.style().name()));
    }

    private TypeScriptConfig.Enums mapEnums(TypeScriptConfigDto.EnumsDto dto) {
        return new TypeScriptConfig.Enums(
                TypeScriptConfig.Enums.EnumStyle.valueOf(dto.style().name()));
    }

    public SpecModel toDomain(SpecModelDto dto) {
        return SpecModel.builder()
                .wrapperType(SpecModel.WrapperType.valueOf(dto.wrapperType().name()))
                .properties(mapProperties(dto.properties()))
                .build();
    }

    private List<SpecProperty> mapProperties(List<SpecModelDto.SpecPropertyDto> dtos) {
        return dtos.stream().map(this::mapProperty).toList();
    }

    private SpecProperty mapProperty(SpecModelDto.SpecPropertyDto dto) {
        return SpecProperty.builder()
                .name(dto.name())
                .required(dto.required())
                .description(dto.description())
                .deprecated(Objects.nonNull(dto.deprecated()) && dto.deprecated())
                .type(mapType(dto.type()))
                .build();
    }

    private SpecType mapType(SpecModelDto.SpecTypeDto dto) {
        return switch (dto) {
            case SpecModelDto.SpecTypeDto.BooleanTypeDto _ -> new BooleanSpecType();

            case SpecModelDto.SpecTypeDto.IntegerTypeDto i ->
                IntegerSpecType.builder()
                        .minimum(i.minimum())
                        .maximum(i.maximum())
                        .build();

            case SpecModelDto.SpecTypeDto.DoubleTypeDto d ->
                DoubleSpecType.builder()
                        .minimum(d.minimum())
                        .maximum(d.maximum())
                        .build();

            case SpecModelDto.SpecTypeDto.DecimalTypeDto d ->
                DecimalSpecType.builder()
                        .minimum(d.minimum())
                        .maximum(d.maximum())
                        .scale(d.scale())
                        .build();

            case SpecModelDto.SpecTypeDto.StringTypeDto s ->
                StringSpecType.builder()
                        .minLength(s.minLength())
                        .maxLength(s.maxLength())
                        .pattern(s.pattern())
                        .examples(s.examples())
                        .format(
                                Objects.isNull(s.format())
                                        ? null
                                        : StringSpecType.StringTypeFormat.valueOf(
                                                s.format().name()))
                        .build();

            case SpecModelDto.SpecTypeDto.EnumTypeDto e ->
                EnumSpecType.builder().values(Set.copyOf(e.values())).build();

            case SpecModelDto.SpecTypeDto.DateTypeDto d ->
                DateSpecType.builder().format(d.format()).build();

            case SpecModelDto.SpecTypeDto.TimeTypeDto t ->
                TimeSpecType.builder().format(t.format()).build();

            case SpecModelDto.SpecTypeDto.DateTimeTypeDto dt ->
                DateTimeSpecType.builder().format(dt.format()).build();

            case SpecModelDto.SpecTypeDto.ObjectTypeDto o ->
                ObjectSpecType.builder().children(mapProperties(o.children())).build();

            case SpecModelDto.SpecTypeDto.ListTypeDto l ->
                ListSpecType.builder()
                        .minItems(l.minItems())
                        .maxItems(l.maxItems())
                        .valueType(mapType(l.valueType()))
                        .build();

            case SpecModelDto.SpecTypeDto.MapTypeDto m ->
                MapSpecType.builder()
                        .keyType(mapType(m.keyType()))
                        .valueType(mapType(m.valueType()))
                        .build();
        };
    }
}
