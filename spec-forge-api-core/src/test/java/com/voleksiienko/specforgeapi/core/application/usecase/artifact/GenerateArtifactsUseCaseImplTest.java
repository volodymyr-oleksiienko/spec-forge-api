package com.voleksiienko.specforgeapi.core.application.usecase.artifact;

import static com.voleksiienko.specforgeapi.core.domain.model.error.JsonMappingErrorCode.JSON_SCHEMA_COMPOSITION_PROPERTY_MERGED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import com.voleksiienko.specforgeapi.core.application.port.in.artifact.command.GenerateFromJsonSampleCommand;
import com.voleksiienko.specforgeapi.core.application.port.in.artifact.command.GenerateFromJsonSchemaCommand;
import com.voleksiienko.specforgeapi.core.application.port.in.artifact.command.GenerateFromSpecModelCommand;
import com.voleksiienko.specforgeapi.core.application.port.in.artifact.result.ArtifactsResult;
import com.voleksiienko.specforgeapi.core.application.port.out.java.JavaModelToJavaCodePort;
import com.voleksiienko.specforgeapi.core.application.port.out.json.*;
import com.voleksiienko.specforgeapi.core.application.service.java.SpecModelToJavaModelMapper;
import com.voleksiienko.specforgeapi.core.domain.exception.SpecModelValidationException;
import com.voleksiienko.specforgeapi.core.domain.model.config.JavaConfig;
import com.voleksiienko.specforgeapi.core.domain.model.conversion.ConversionResult;
import com.voleksiienko.specforgeapi.core.domain.model.conversion.Warning;
import com.voleksiienko.specforgeapi.core.domain.model.java.JavaClass;
import com.voleksiienko.specforgeapi.core.domain.model.spec.SpecModel;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class GenerateArtifactsUseCaseImplTest {

    private final String jsonSample = "{\"field\": \"1\"}";
    private final String jsonSchema = "{\"type\": \"object\"}";
    private final String expectedCode = "code";
    private final SpecModel mockModel = mock(SpecModel.class);
    private final JavaConfig config = mock(JavaConfig.class);
    private final JavaClass javaClass = mock(JavaClass.class);
    private final List<Warning> warnings = List.of(new Warning("warning", JSON_SCHEMA_COMPOSITION_PROPERTY_MERGED));

    @Mock
    private JsonSchemaValidatorPort jsonSchemaValidator;

    @Mock
    private JsonSchemaToSpecModelPort schemaParser;

    @Mock
    private JsonSampleToJsonSchemaPort sampleMapper;

    @Mock
    private SpecModelToJsonSamplePort jsonSampleGenerator;

    @Mock
    private SpecModelToJsonSchemaPort jsonSchemaGenerator;

    @Mock
    private SpecModelToJavaModelMapper javaModelMapper;

    @Mock
    private JavaModelToJavaCodePort javaCodeMapper;

    @InjectMocks
    private GenerateArtifactsUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        when(schemaParser.map(jsonSchema)).thenReturn(new ConversionResult(mockModel, warnings));
        when(sampleMapper.map(jsonSample)).thenReturn(jsonSchema);
        when(jsonSampleGenerator.map(mockModel)).thenReturn(jsonSample);
        when(jsonSchemaGenerator.map(mockModel)).thenReturn(jsonSchema);
        when(javaModelMapper.map(mockModel, config)).thenReturn(javaClass);
        when(javaCodeMapper.map(javaClass)).thenReturn(expectedCode);
    }

    @Test
    void shouldGenerateArtifactsFromJsonSchema() {
        var result = useCase.generateFromJsonSchema(new GenerateFromJsonSchemaCommand(jsonSchema, config));

        verifyResult(result);
        verify(jsonSchemaValidator).validate(jsonSchema);
        verify(schemaParser).map(jsonSchema);
        assertThat(result.warnings()).isEqualTo(warnings);
    }

    @Test
    void shouldThrowWhenJsonSchemaValidationFails() {
        var schema = "invalid";
        GenerateFromJsonSchemaCommand command = new GenerateFromJsonSchemaCommand(schema, config);
        doThrow(new SpecModelValidationException("Fail"))
                .when(jsonSchemaValidator)
                .validate(schema);

        assertThatThrownBy(() -> useCase.generateFromJsonSchema(command))
                .isInstanceOf(SpecModelValidationException.class)
                .hasMessage("Fail");

        verify(schemaParser, never()).map(anyString());
    }

    @Test
    void shouldGenerateArtifactsFromJsonSample() {
        var result = useCase.generateFromJsonSample(new GenerateFromJsonSampleCommand(jsonSample, config));

        verifyResult(result);
        verify(schemaParser).map(jsonSchema);
        assertThat(result.warnings()).isEqualTo(warnings);
    }

    @Test
    void shouldGenerateArtifactsFromSpecModel() {
        var result = useCase.generateFromSpecModel(new GenerateFromSpecModelCommand(mockModel, config));

        verifyResult(result);
    }

    @Test
    void shouldGenerateArtifactsWithoutCodeWhenConfigIsNull() {
        var result = useCase.generateFromJsonSchema(new GenerateFromJsonSchemaCommand(jsonSchema, null));

        verify(javaModelMapper, never()).map(any(), any());
        verify(javaCodeMapper, never()).map(any());
        verify(schemaParser).map(jsonSchema);
        assertThat(result.code()).isNull();
        assertThat(result.jsonSchema()).isEqualTo(jsonSchema);
    }

    private void verifyResult(ArtifactsResult result) {
        verify(jsonSampleGenerator).map(mockModel);
        verify(jsonSchemaGenerator).map(mockModel);
        verify(javaModelMapper).map(mockModel, config);
        verify(javaCodeMapper).map(javaClass);
        assertThat(result.specModel()).isEqualTo(mockModel);
        assertThat(result.jsonSample()).isEqualTo(jsonSample);
        assertThat(result.code()).isEqualTo(expectedCode);
        assertThat(result.jsonSchema()).isEqualTo(jsonSchema);
    }
}
