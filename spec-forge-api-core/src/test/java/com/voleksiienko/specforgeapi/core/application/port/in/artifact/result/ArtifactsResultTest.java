package com.voleksiienko.specforgeapi.core.application.port.in.artifact.result;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

import com.voleksiienko.specforgeapi.core.domain.model.conversion.Warning;
import com.voleksiienko.specforgeapi.core.domain.model.spec.SpecModel;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

class ArtifactsResultTest {

    private final SpecModel mockSpec = mock(SpecModel.class);
    private final Warning warning = mock(Warning.class);
    private final List<Warning> warnings = List.of(warning);

    @Test
    void shouldCreateResult() {

        var result = new ArtifactsResult(mockSpec, "{}", "{}", "public record Test() {}", List.of(warning));

        assertThat(result.specModel()).isEqualTo(mockSpec);
        assertThat(result.jsonSchema()).isEqualTo("{}");
        assertThat(result.jsonSample()).isEqualTo("{}");
        assertThat(result.code()).isEqualTo("public record Test() {}");
        assertThat(result.warnings()).hasSize(1).containsExactlyInAnyOrder(warning);
    }

    @Test
    void shouldFailOnNullSpec() {
        assertThatThrownBy(() -> new ArtifactsResult(null, "{}", "{}", "code", warnings))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("specModel is mandatory");
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", "   "})
    void shouldFailOnBlankJsonArtifacts(String blank) {
        assertThatThrownBy(() -> new ArtifactsResult(mockSpec, blank, "{}", "code", warnings))
                .isInstanceOf(RuntimeException.class);

        assertThatThrownBy(() -> new ArtifactsResult(mockSpec, "{}", blank, "code", warnings))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void shouldAllowNullCode() {
        var result = new ArtifactsResult(mockSpec, "{}", "{}", null, List.of(warning));
        assertThat(result.code()).isNull();
    }

    @Test
    void shouldAllowNullWarnings() {
        var result = new ArtifactsResult(mockSpec, "{}", "{}", "code", null);
        assertThat(result.warnings()).isNull();
    }
}
