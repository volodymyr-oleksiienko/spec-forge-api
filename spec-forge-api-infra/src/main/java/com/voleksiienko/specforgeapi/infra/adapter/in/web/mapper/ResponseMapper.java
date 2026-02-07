package com.voleksiienko.specforgeapi.infra.adapter.in.web.mapper;

import com.voleksiienko.specforgeapi.core.application.port.in.artifact.result.ArtifactsResult;
import com.voleksiienko.specforgeapi.core.domain.model.spec.SpecModel;
import com.voleksiienko.specforgeapi.core.domain.model.spec.SpecProperty;
import com.voleksiienko.specforgeapi.core.domain.model.spec.type.*;
import com.voleksiienko.specforgeapi.infra.adapter.in.web.dto.SpecModelDto;
import com.voleksiienko.specforgeapi.infra.adapter.in.web.dto.SpecModelDto.SpecPropertyDto;
import com.voleksiienko.specforgeapi.infra.adapter.in.web.dto.SpecModelDto.SpecTypeDto;
import com.voleksiienko.specforgeapi.infra.adapter.in.web.dto.response.ArtifactsResponse;
import java.util.List;
import java.util.Objects;
import org.springframework.stereotype.Component;

@Component
public class ResponseMapper {

    public ArtifactsResponse map(ArtifactsResult result) {
        List<ArtifactsResponse.Warning> warnings = mapWarnings(result);
        SpecModelDto specModelDto = mapSpecModel(result.specModel());
        return new ArtifactsResponse(specModelDto, warnings);
    }

    private List<ArtifactsResponse.Warning> mapWarnings(ArtifactsResult result) {
        return result.warnings().stream()
                .map(warning -> new ArtifactsResponse.Warning(
                        warning.devMessage(), warning.errorCode().getName()))
                .toList();
    }

    private SpecModelDto mapSpecModel(SpecModel domainModel) {
        return new SpecModelDto(
                SpecModelDto.WrapperType.valueOf(domainModel.getWrapperType().name()),
                mapProperties(domainModel.getProperties()));
    }

    private List<SpecPropertyDto> mapProperties(List<SpecProperty> properties) {
        return Objects.nonNull(properties)
                ? properties.stream().map(this::mapProperty).toList()
                : List.of();
    }

    private SpecPropertyDto mapProperty(SpecProperty property) {
        return new SpecPropertyDto(
                property.getName(),
                mapType(property.getType()),
                property.isRequired(),
                property.getDescription(),
                property.isDeprecated() ? true : null);
    }

    private SpecTypeDto mapType(SpecType type) {
        return switch (type) {
            case BooleanSpecType t -> new SpecTypeDto.BooleanTypeDto(t.getExamples());

            case IntegerSpecType t -> new SpecTypeDto.IntegerTypeDto(t.getMinimum(), t.getMaximum(), t.getExamples());

            case DoubleSpecType t -> new SpecTypeDto.DoubleTypeDto(t.getMinimum(), t.getMaximum(), t.getExamples());

            case DecimalSpecType t ->
                new SpecTypeDto.DecimalTypeDto(t.getMinimum(), t.getMaximum(), t.getScale(), t.getExamples());

            case StringSpecType t ->
                new SpecTypeDto.StringTypeDto(
                        t.getMinLength(),
                        t.getMaxLength(),
                        t.getPattern(),
                        Objects.nonNull(t.getFormat())
                                ? SpecTypeDto.StringTypeDto.StringTypeFormat.valueOf(
                                        t.getFormat().name())
                                : null,
                        t.getExamples());

            case EnumSpecType t ->
                new SpecTypeDto.EnumTypeDto(t.getValues().stream().sorted().toList(), t.getExamples());

            case DateSpecType t -> new SpecTypeDto.DateTypeDto(t.getFormat(), t.getExamples());
            case TimeSpecType t -> new SpecTypeDto.TimeTypeDto(t.getFormat(), t.getExamples());
            case DateTimeSpecType t -> new SpecTypeDto.DateTimeTypeDto(t.getFormat(), t.getExamples());

            case ObjectSpecType t -> new SpecTypeDto.ObjectTypeDto(mapProperties(t.getChildren()));

            case ListSpecType t ->
                new SpecTypeDto.ListTypeDto(t.getMinItems(), t.getMaxItems(), mapType(t.getValueType()));

            case MapSpecType t -> new SpecTypeDto.MapTypeDto(mapType(t.getKeyType()), mapType(t.getValueType()));
        };
    }
}
