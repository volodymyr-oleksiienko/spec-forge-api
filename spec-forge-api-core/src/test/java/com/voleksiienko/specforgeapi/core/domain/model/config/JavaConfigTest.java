package com.voleksiienko.specforgeapi.core.domain.model.config;

import static com.voleksiienko.specforgeapi.core.domain.model.config.JavaConfig.Serialization.JsonPropertyMode.ALWAYS;
import static com.voleksiienko.specforgeapi.core.domain.model.config.JavaConfig.Structure.Type.CLASS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

import com.voleksiienko.specforgeapi.core.domain.exception.ConfigValidationException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class JavaConfigTest {

    private final BaseConfig validBase = mock(BaseConfig.class);
    private final JavaConfig.Structure validStructure = new JavaConfig.Structure(CLASS);
    private final JavaConfig.Validation validValidation = new JavaConfig.Validation(true);
    private final JavaConfig.Builder validBuilder = new JavaConfig.Builder(true, false);
    private final JavaConfig.Serialization validSerialization = new JavaConfig.Serialization(ALWAYS);

    @Nested
    class RootValidation {

        @Test
        void shouldCreateValidConfig() {
            var config = new JavaConfig(validBase, validStructure, validValidation, validBuilder, validSerialization);

            assertThat(config.base()).isEqualTo(validBase);
            assertThat(config.structure()).isEqualTo(validStructure);
        }

        @Test
        void shouldThrowWhenBaseIsNull() {
            assertThatThrownBy(() ->
                            new JavaConfig(null, validStructure, validValidation, validBuilder, validSerialization))
                    .isInstanceOf(ConfigValidationException.class)
                    .hasMessage("Base configuration is mandatory");
        }

        @Test
        void shouldThrowWhenStructureIsNull() {
            assertThatThrownBy(() -> new JavaConfig(validBase, null, validValidation, validBuilder, validSerialization))
                    .isInstanceOf(ConfigValidationException.class)
                    .hasMessage("Structure configuration is mandatory");
        }

        @Test
        void shouldThrowWhenValidationIsNull() {
            assertThatThrownBy(() -> new JavaConfig(validBase, validStructure, null, validBuilder, validSerialization))
                    .isInstanceOf(ConfigValidationException.class)
                    .hasMessage("Validation configuration is mandatory");
        }

        @Test
        void shouldThrowWhenBuilderIsNull() {
            assertThatThrownBy(
                            () -> new JavaConfig(validBase, validStructure, validValidation, null, validSerialization))
                    .isInstanceOf(ConfigValidationException.class)
                    .hasMessage("Builder configuration is mandatory");
        }

        @Test
        void shouldThrowWhenSerializationIsNull() {
            assertThatThrownBy(() -> new JavaConfig(validBase, validStructure, validValidation, validBuilder, null))
                    .isInstanceOf(ConfigValidationException.class)
                    .hasMessage("Serialization configuration is mandatory");
        }
    }

    @Nested
    class StructureValidation {

        @Test
        void shouldCreateWithValidType() {
            var structure = new JavaConfig.Structure(JavaConfig.Structure.Type.RECORD);
            assertThat(structure.type()).isEqualTo(JavaConfig.Structure.Type.RECORD);
        }

        @Test
        void shouldThrowWhenTypeIsNull() {
            assertThatThrownBy(() -> new JavaConfig.Structure(null))
                    .isInstanceOf(ConfigValidationException.class)
                    .hasMessage("Structure type is mandatory");
        }
    }

    @Nested
    class SerializationValidation {

        @Test
        void shouldCreateWithValidMode() {
            var serialization = new JavaConfig.Serialization(JavaConfig.Serialization.JsonPropertyMode.NEVER);
            assertThat(serialization.jsonPropertyMode()).isEqualTo(JavaConfig.Serialization.JsonPropertyMode.NEVER);
        }

        @Test
        void shouldThrowWhenModeIsNull() {
            assertThatThrownBy(() -> new JavaConfig.Serialization(null))
                    .isInstanceOf(ConfigValidationException.class)
                    .hasMessage("JSON Property mode is mandatory");
        }
    }
}
