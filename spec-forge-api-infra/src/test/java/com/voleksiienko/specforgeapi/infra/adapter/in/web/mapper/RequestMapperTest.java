package com.voleksiienko.specforgeapi.infra.adapter.in.web.mapper;

import static com.voleksiienko.specforgeapi.infra.adapter.in.web.dto.request.BaseConfigDto.FieldsDto.SortTypeDto.ALPHABETICAL;
import static com.voleksiienko.specforgeapi.infra.adapter.in.web.dto.request.JavaConfigDto.SerializationDto.JsonPropertyModeDto.ALWAYS;
import static com.voleksiienko.specforgeapi.infra.adapter.in.web.dto.request.JavaConfigDto.StructureDto.TypeDto.CLASS;
import static org.assertj.core.api.Assertions.assertThat;

import com.voleksiienko.specforgeapi.core.application.port.in.artifact.command.GenerateFromJsonSampleCommand;
import com.voleksiienko.specforgeapi.core.application.port.in.artifact.command.GenerateFromJsonSchemaCommand;
import com.voleksiienko.specforgeapi.core.domain.model.config.BaseConfig;
import com.voleksiienko.specforgeapi.core.domain.model.config.JavaConfig;
import com.voleksiienko.specforgeapi.infra.adapter.in.web.dto.request.BaseConfigDto;
import com.voleksiienko.specforgeapi.infra.adapter.in.web.dto.request.GenerateFromRawRequest;
import com.voleksiienko.specforgeapi.infra.adapter.in.web.dto.request.JavaConfigDto;
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
}
