package com.voleksiienko.specforgeapi.core.domain.model.config;

import static com.voleksiienko.specforgeapi.core.domain.model.config.TypeScriptConfig.Enums.EnumStyle.TS_ENUM;
import static com.voleksiienko.specforgeapi.core.domain.model.config.TypeScriptConfig.Structure.DeclarationStyle.INTERFACE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

import com.voleksiienko.specforgeapi.core.domain.exception.ConfigValidationException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class TypeScriptConfigTest {

    private final BaseConfig validBase = mock(BaseConfig.class);
    private final TypeScriptConfig.Structure validStructure = new TypeScriptConfig.Structure(INTERFACE);
    private final TypeScriptConfig.Enums validEnums = new TypeScriptConfig.Enums(TS_ENUM);

    @Nested
    class RootValidation {

        @Test
        void shouldCreateValidConfig() {
            var config = new TypeScriptConfig(validBase, validStructure, validEnums);

            assertThat(config.base()).isEqualTo(validBase);
            assertThat(config.structure()).isEqualTo(validStructure);
            assertThat(config.enums()).isEqualTo(validEnums);
        }

        @Test
        void shouldThrowWhenBaseIsNull() {
            assertThatThrownBy(() -> new TypeScriptConfig(null, validStructure, validEnums))
                    .isInstanceOf(ConfigValidationException.class)
                    .hasMessage("Base configuration is mandatory");
        }

        @Test
        void shouldThrowWhenStructureIsNull() {
            assertThatThrownBy(() -> new TypeScriptConfig(validBase, null, validEnums))
                    .isInstanceOf(ConfigValidationException.class)
                    .hasMessage("Structure configuration is mandatory");
        }

        @Test
        void shouldThrowWhenEnumsIsNull() {
            assertThatThrownBy(() -> new TypeScriptConfig(validBase, validStructure, null))
                    .isInstanceOf(ConfigValidationException.class)
                    .hasMessage("Enums configuration is mandatory");
        }
    }

    @Nested
    class StructureValidation {

        @Test
        void shouldCreateWithValidStyle() {
            var structure = new TypeScriptConfig.Structure(TypeScriptConfig.Structure.DeclarationStyle.TYPE_ALIAS);
            assertThat(structure.style()).isEqualTo(TypeScriptConfig.Structure.DeclarationStyle.TYPE_ALIAS);
        }

        @Test
        void shouldThrowWhenStyleIsNull() {
            assertThatThrownBy(() -> new TypeScriptConfig.Structure(null))
                    .isInstanceOf(ConfigValidationException.class)
                    .hasMessage("Declaration style is mandatory");
        }
    }

    @Nested
    class EnumsValidation {

        @Test
        void shouldCreateWithValidStyle() {
            var enums = new TypeScriptConfig.Enums(TypeScriptConfig.Enums.EnumStyle.UNION_STRING);
            assertThat(enums.style()).isEqualTo(TypeScriptConfig.Enums.EnumStyle.UNION_STRING);
        }

        @Test
        void shouldThrowWhenStyleIsNull() {
            assertThatThrownBy(() -> new TypeScriptConfig.Enums(null))
                    .isInstanceOf(ConfigValidationException.class)
                    .hasMessage("Enum style is mandatory");
        }
    }
}
