package com.voleksiienko.specforgeapi.infra.adapter.in.web.mapper;

import static com.voleksiienko.specforgeapi.infra.adapter.in.web.dto.request.BaseConfigDto.FieldsDto.SortTypeDto.ALPHABETICAL;
import static com.voleksiienko.specforgeapi.infra.adapter.in.web.dto.request.JavaConfigDto.SerializationDto.JsonPropertyModeDto.ALWAYS;
import static com.voleksiienko.specforgeapi.infra.adapter.in.web.dto.request.JavaConfigDto.StructureDto.TypeDto.CLASS;
import static com.voleksiienko.specforgeapi.infra.adapter.in.web.dto.request.TypeScriptConfigDto.EnumsDto.EnumStyle.TS_ENUM;
import static com.voleksiienko.specforgeapi.infra.adapter.in.web.dto.request.TypeScriptConfigDto.StructureDto.DeclarationStyle.INTERFACE;
import static org.assertj.core.api.Assertions.assertThat;

import com.voleksiienko.specforgeapi.core.application.port.in.artifact.command.GenerateFromJsonSampleCommand;
import com.voleksiienko.specforgeapi.core.application.port.in.artifact.command.GenerateFromJsonSchemaCommand;
import com.voleksiienko.specforgeapi.core.domain.model.config.BaseConfig;
import com.voleksiienko.specforgeapi.core.domain.model.config.JavaConfig;
import com.voleksiienko.specforgeapi.core.domain.model.config.TypeScriptConfig;
import com.voleksiienko.specforgeapi.core.domain.model.spec.type.ObjectSpecType;
import com.voleksiienko.specforgeapi.core.domain.model.spec.type.StringSpecType;
import com.voleksiienko.specforgeapi.infra.adapter.in.web.dto.SpecModelDto;
import com.voleksiienko.specforgeapi.infra.adapter.in.web.dto.request.*;
import java.util.List;
import org.junit.jupiter.api.Test;

class RequestMapperTest {

    private final RequestMapper mapper = new RequestMapper();

    private final BaseConfigDto baseConfigDto =
            new BaseConfigDto(new BaseConfigDto.NamingDto("UserDto"), new BaseConfigDto.FieldsDto(ALPHABETICAL));
    private final JavaConfigDto javaConfigDto = new JavaConfigDto(
            baseConfigDto,
            new JavaConfigDto.StructureDto(CLASS),
            new JavaConfigDto.ValidationDto(true),
            new JavaConfigDto.BuilderDto(true, true),
            new JavaConfigDto.SerializationDto(ALWAYS));
    private final GenerateFromRawRequest requestWithConfig = new GenerateFromRawRequest("{}", javaConfigDto);
    private final GenerateFromRawRequest requestWithoutConfig = new GenerateFromRawRequest("{}", null);
    private final TypeScriptConfigDto tsConfigDto = new TypeScriptConfigDto(
            baseConfigDto, new TypeScriptConfigDto.StructureDto(INTERFACE), new TypeScriptConfigDto.EnumsDto(TS_ENUM));
    private final GenerateFromRawRequest requestWithTsConfig = new GenerateFromRawRequest("{}", tsConfigDto);

    @Test
    void shouldMapSchemaCommandWithConfig() {
        GenerateFromJsonSchemaCommand command = mapper.toGenerateFromJsonSchemaCommand(requestWithConfig);

        assertThat(command.jsonSchema()).isEqualTo("{}");
        assertThat(command.config()).isInstanceOf(JavaConfig.class);
        assertJavaConfigMatches((JavaConfig) command.config());
    }

    @Test
    void shouldMapSampleCommandWithConfig() {
        GenerateFromJsonSampleCommand command = mapper.toGenerateFromJsonSampleCommand(requestWithConfig);

        assertThat(command.jsonSample()).isEqualTo("{}");
        assertThat(command.config()).isInstanceOf(JavaConfig.class);
        assertJavaConfigMatches((JavaConfig) command.config());
    }

    @Test
    void shouldMapSchemaCommandWithoutConfig() {
        GenerateFromJsonSchemaCommand command = mapper.toGenerateFromJsonSchemaCommand(requestWithoutConfig);

        assertThat(command.jsonSchema()).isEqualTo("{}");
        assertThat(command.config()).isNull();
    }

    @Test
    void shouldMapSampleCommandWithoutConfig() {
        GenerateFromJsonSampleCommand command = mapper.toGenerateFromJsonSampleCommand(requestWithoutConfig);

        assertThat(command.jsonSample()).isEqualTo("{}");
        assertThat(command.config()).isNull();
    }

    @Test
    void shouldMapSchemaCommandWithTypeScriptConfig() {
        GenerateFromJsonSchemaCommand command = mapper.toGenerateFromJsonSchemaCommand(requestWithTsConfig);

        assertThat(command.jsonSchema()).isEqualTo("{}");
        assertThat(command.config()).isInstanceOf(TypeScriptConfig.class);
        assertTypeScriptConfigMatches((TypeScriptConfig) command.config());
    }

    @Test
    void shouldMapSampleCommandWithTypeScriptConfig() {
        GenerateFromJsonSampleCommand command = mapper.toGenerateFromJsonSampleCommand(requestWithTsConfig);

        assertThat(command.jsonSample()).isEqualTo("{}");
        assertThat(command.config()).isInstanceOf(TypeScriptConfig.class);
        assertTypeScriptConfigMatches((TypeScriptConfig) command.config());
    }

    @Test
    void shouldMapGenerateFromSpecModelCommand() {
        var propDto = new SpecModelDto.SpecPropertyDto(
                "id", new SpecModelDto.SpecTypeDto.IntegerTypeDto(null, null, null), true, null, null);
        var specModelDto = new SpecModelDto(SpecModelDto.WrapperType.OBJECT, List.of(propDto));
        var request = new GenerateFromSpecModelRequest(specModelDto, tsConfigDto);

        var command = mapper.toGenerateFromSpecModelCommand(request);

        assertThat(command.specModel().getProperties()).hasSize(1);
        assertThat(command.specModel().getProperties().getFirst().getName()).isEqualTo("id");
        assertThat(command.config()).isInstanceOf(TypeScriptConfig.class);
    }

    @Test
    void shouldHandleRecursiveNesting() {
        var stringType = new SpecModelDto.SpecTypeDto.StringTypeDto(null, null, null, null, null);
        var innerProp = new SpecModelDto.SpecPropertyDto("inner", stringType, true, null, null);
        var objectType = new SpecModelDto.SpecTypeDto.ObjectTypeDto(List.of(innerProp));

        var rootProp = new SpecModelDto.SpecPropertyDto("root", objectType, true, null, null);
        var dto = new SpecModelDto(SpecModelDto.WrapperType.OBJECT, List.of(rootProp));

        var domain = mapper.toDomain(dto);

        var rootType = (ObjectSpecType) domain.getProperties().getFirst().getType();
        assertThat(rootType.getChildren().getFirst().getName()).isEqualTo("inner");
        assertThat(rootType.getChildren().getFirst().getType()).isInstanceOf(StringSpecType.class);
    }

    private void assertJavaConfigMatches(JavaConfig config) {
        assertThat(config.base().naming().className()).isEqualTo("UserDto");
        assertThat(config.base().fields().sort()).isEqualTo(BaseConfig.Fields.SortType.ALPHABETICAL);
        assertThat(config.structure().type()).isEqualTo(JavaConfig.Structure.Type.CLASS);
        assertThat(config.validation().enabled()).isTrue();
        assertThat(config.builder().enabled()).isTrue();
        assertThat(config.builder().onlyIfMultipleFields()).isTrue();
        assertThat(config.serialization().jsonPropertyMode())
                .isEqualTo(JavaConfig.Serialization.JsonPropertyMode.ALWAYS);
    }

    private void assertTypeScriptConfigMatches(TypeScriptConfig config) {
        assertThat(config.base().naming().className()).isEqualTo("UserDto");
        assertThat(config.base().fields().sort()).isEqualTo(BaseConfig.Fields.SortType.ALPHABETICAL);
        assertThat(config.structure().style()).isEqualTo(TypeScriptConfig.Structure.DeclarationStyle.INTERFACE);
        assertThat(config.enums().style()).isEqualTo(TypeScriptConfig.Enums.EnumStyle.TS_ENUM);
    }
}
