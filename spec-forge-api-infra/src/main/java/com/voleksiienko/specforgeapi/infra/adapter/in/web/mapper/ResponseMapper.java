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
                mapProperties(property.getChildren()),
                property.getDescription(),
                property.getExamples(),
                property.isDeprecated() ? true : null);
    }

    private SpecTypeDto mapType(SpecType type) {
        return switch (type) {
            case BooleanSpecType ignored -> new SpecTypeDto.BooleanTypeDto();

            case IntegerSpecType t -> new SpecTypeDto.IntegerTypeDto(t.getMinimum(), t.getMaximum());

            case DoubleSpecType t -> new SpecTypeDto.DoubleTypeDto(t.getMinimum(), t.getMaximum());

            case DecimalSpecType t -> new SpecTypeDto.DecimalTypeDto(t.getMinimum(), t.getMaximum(), t.getScale());

            case StringSpecType t ->
                new SpecTypeDto.StringTypeDto(
                        t.getMinLength(),
                        t.getMaxLength(),
                        t.getPattern(),
                        Objects.nonNull(t.getFormat())
                                ? SpecTypeDto.StringTypeDto.StringTypeFormat.valueOf(
                                        t.getFormat().name())
                                : null);

            case EnumSpecType t ->
                new SpecTypeDto.EnumTypeDto(t.getValues().stream().sorted().toList());

            case DateSpecType t -> new SpecTypeDto.DateTypeDto(t.getFormat());
            case TimeSpecType t -> new SpecTypeDto.TimeTypeDto(t.getFormat());
            case DateTimeSpecType t -> new SpecTypeDto.DateTimeTypeDto(t.getFormat());

            case ObjectSpecType ignored -> new SpecTypeDto.ObjectTypeDto();

            case ListSpecType t ->
                new SpecTypeDto.ListTypeDto(t.getMinItems(), t.getMaxItems(), mapType(t.getValueType()));

            case MapSpecType t -> new SpecTypeDto.MapTypeDto(mapType(t.getKeyType()), mapType(t.getValueType()));
        };
    }
}
