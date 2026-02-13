package com.voleksiienko.specforgeapi.infra.adapter.in.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.voleksiienko.specforgeapi.core.application.exception.ConversionException;
import com.voleksiienko.specforgeapi.core.application.port.in.artifact.GenerateArtifactsUseCase;
import com.voleksiienko.specforgeapi.core.application.port.in.artifact.command.GenerateFromJsonSampleCommand;
import com.voleksiienko.specforgeapi.core.application.port.in.artifact.command.GenerateFromJsonSchemaCommand;
import com.voleksiienko.specforgeapi.core.application.port.in.artifact.result.ArtifactsResult;
import com.voleksiienko.specforgeapi.core.domain.exception.ConfigValidationException;
import com.voleksiienko.specforgeapi.core.domain.exception.JavaModelValidationException;
import com.voleksiienko.specforgeapi.core.domain.exception.SpecModelValidationException;
import com.voleksiienko.specforgeapi.core.domain.model.error.JsonMappingErrorCode;
import com.voleksiienko.specforgeapi.core.domain.model.spec.SpecModel;
import com.voleksiienko.specforgeapi.core.domain.model.spec.SpecProperty;
import com.voleksiienko.specforgeapi.core.domain.model.spec.type.BooleanSpecType;
import com.voleksiienko.specforgeapi.infra.adapter.in.web.dto.request.BaseConfigDto;
import com.voleksiienko.specforgeapi.infra.adapter.in.web.dto.request.GenerateFromRawRequest;
import com.voleksiienko.specforgeapi.infra.adapter.in.web.dto.request.JavaConfigDto;
import com.voleksiienko.specforgeapi.infra.adapter.in.web.mapper.RequestMapper;
import com.voleksiienko.specforgeapi.infra.adapter.in.web.mapper.ResponseMapper;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.json.JsonMapper;

@WebMvcTest(GenerationController.class)
@Import({ResponseMapper.class, RequestMapper.class})
class GenerationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JsonMapper jsonMapper;

    @MockitoBean
    private GenerateArtifactsUseCase useCase;

    static Stream<Arguments> successScenarios() {
        return Stream.of(Arguments.of("/artifacts/from-json-schema"), Arguments.of("/artifacts/from-json-sample"));
    }

    static Stream<Arguments> errorScenarios() {
        var validRequest = """
            {
              "content": "{\\"valid\\": \\"json\\"}"
            }
            """;
        return Stream.of(
                Arguments.of(
                        "Domain Validation Error",
                        new SpecModelValidationException("Bad Rule"),
                        400,
                        "SPEC_MODEL_VALIDATION_FAILED",
                        validRequest),
                Arguments.of(
                        "Domain Validation Error",
                        new JavaModelValidationException("Parse Error"),
                        400,
                        "JAVA_MODEL_VALIDATION_FAILED",
                        validRequest),
                Arguments.of(
                        "Domain Validation Error",
                        new ConfigValidationException("Parse Error"),
                        400,
                        "CONFIG_VALIDATION_FAILED",
                        validRequest),
                Arguments.of(
                        "Conversion Error",
                        new ConversionException("Parse Error", JsonMappingErrorCode.JSON_SCHEMA_PARSING_FAILED),
                        400,
                        "JSON_SCHEMA_PARSING_FAILED",
                        validRequest),
                Arguments.of("Unexpected 500", new RuntimeException("DB is down"), 500, "INTERNAL", validRequest),
                Arguments.of("Malformed JSON Body", null, 400, "INVALID_REQUEST_FORMAT", "{ broken_json: "),
                Arguments.of("Empty Request Content", null, 400, "INVALID_REQUEST_FORMAT", "{\"content\": \"\"}"));
    }

    @ParameterizedTest(name = "[{index}] Endpoint: {0}")
    @MethodSource("successScenarios")
    void shouldGenerateArtifactsSuccessfully(String url) throws Exception {
        var request = new GenerateFromRawRequest(
                "{\"some\":\"json\"}",
                new JavaConfigDto(
                        new BaseConfigDto(
                                new BaseConfigDto.NamingDto("J"),
                                new BaseConfigDto.FieldsDto(BaseConfigDto.FieldsDto.SortTypeDto.ALPHABETICAL)),
                        new JavaConfigDto.StructureDto(JavaConfigDto.StructureDto.TypeDto.RECORD),
                        new JavaConfigDto.ValidationDto(true),
                        new JavaConfigDto.BuilderDto(true, false),
                        new JavaConfigDto.SerializationDto(JavaConfigDto.SerializationDto.JsonPropertyModeDto.ALWAYS)));
        var mockResult = new ArtifactsResult(
                SpecModel.builder()
                        .wrapperType(SpecModel.WrapperType.OBJECT)
                        .properties(List.of(SpecProperty.builder()
                                .name("active")
                                .type(new BooleanSpecType())
                                .build()))
                        .build(),
                "{\"active\": \"true\"}",
                "{\"type\": \"object\", \"properties\": { \"active\" : \"boolean\" }}",
                "code",
                List.of());

        when(useCase.generateFromJsonSchema(any(GenerateFromJsonSchemaCommand.class)))
                .thenReturn(mockResult);
        when(useCase.generateFromJsonSample(any(GenerateFromJsonSampleCommand.class)))
                .thenReturn(mockResult);

        mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.specModel.wrapperType").value("OBJECT"));
    }

    @ParameterizedTest(name = "[{index}] {0} Test -> Expect {1} {2}")
    @MethodSource("errorScenarios")
    void shouldHandleExceptionsCorrectly(
            String testName,
            Exception exceptionToThrow,
            int expectedStatus,
            String expectedErrorCode,
            String requestContent)
            throws Exception {
        if (exceptionToThrow != null) {
            when(useCase.generateFromJsonSchema(any())).thenThrow(exceptionToThrow);
        }

        mockMvc.perform(post("/artifacts/from-json-schema")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestContent))
                .andExpect(status().is(expectedStatus))
                .andExpect(jsonPath("$.code").value(expectedErrorCode));
    }
}
