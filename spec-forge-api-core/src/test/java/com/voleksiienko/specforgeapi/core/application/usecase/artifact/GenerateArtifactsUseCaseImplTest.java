package com.voleksiienko.specforgeapi.core.application.usecase.artifact;

import static com.voleksiienko.specforgeapi.core.domain.model.error.JsonMappingErrorCode.JSON_SCHEMA_COMPOSITION_PROPERTY_MERGED;
import static com.voleksiienko.specforgeapi.core.domain.model.error.JsonMappingErrorCode.JSON_SCHEMA_MAP_KEY_DEFAULTED_TO_STRING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import com.voleksiienko.specforgeapi.core.application.port.in.artifact.command.GenerateFromJsonSampleCommand;
import com.voleksiienko.specforgeapi.core.application.port.in.artifact.command.GenerateFromJsonSchemaCommand;
import com.voleksiienko.specforgeapi.core.application.port.out.json.JsonSampleToJsonSchemaPort;
import com.voleksiienko.specforgeapi.core.application.port.out.json.JsonSchemaToSpecModelPort;
import com.voleksiienko.specforgeapi.core.application.port.out.json.JsonSchemaValidatorPort;
import com.voleksiienko.specforgeapi.core.domain.exception.SpecModelValidationException;
import com.voleksiienko.specforgeapi.core.domain.model.conversion.ConversionResult;
import com.voleksiienko.specforgeapi.core.domain.model.conversion.Warning;
import com.voleksiienko.specforgeapi.core.domain.model.spec.SpecModel;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GenerateArtifactsUseCaseImplTest {

    @Mock
    private JsonSchemaValidatorPort jsonSchemaValidator;

    @Mock
    private JsonSchemaToSpecModelPort schemaParser;

    @Mock
    private JsonSampleToJsonSchemaPort sampleMapper;

    @InjectMocks
    private GenerateArtifactsUseCaseImpl useCase;

    @Test
    void shouldGenerateArtifactsFromJsonSchema() {
        var schema = "{}";
        var mockModel = mock(SpecModel.class);
        var warnings = List.of(new Warning("warning", JSON_SCHEMA_COMPOSITION_PROPERTY_MERGED));
        when(schemaParser.map(schema)).thenReturn(new ConversionResult(mockModel, warnings));

        var result = useCase.generateFromJsonSchema(new GenerateFromJsonSchemaCommand(schema));

        verify(jsonSchemaValidator).validate(schema);
        verify(schemaParser).map(schema);
        assertThat(result.specModel()).isEqualTo(mockModel);
        assertThat(result.warnings()).isEqualTo(warnings);
    }

    @Test
    void shouldThrowWhenJsonSchemaValidationFails() {
        var schema = "invalid";
        GenerateFromJsonSchemaCommand command = new GenerateFromJsonSchemaCommand(schema);
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
        var sample = "{\"key\":\"val\"}";
        var inferredSchema = "{\"type\":\"inferred\"}";
        var mockModel = mock(SpecModel.class);
        var warnings = List.of(new Warning("warning", JSON_SCHEMA_MAP_KEY_DEFAULTED_TO_STRING));
        when(sampleMapper.map(sample)).thenReturn(inferredSchema);
        when(schemaParser.map(inferredSchema)).thenReturn(new ConversionResult(mockModel, warnings));

        var result = useCase.generateFromJsonSample(new GenerateFromJsonSampleCommand(sample));

        verify(sampleMapper).map(sample);
        verify(schemaParser).map(inferredSchema);
        verify(jsonSchemaValidator, never()).validate(anyString());
        assertThat(result.specModel()).isEqualTo(mockModel);
        assertThat(result.warnings()).isEqualTo(warnings);
    }
}
