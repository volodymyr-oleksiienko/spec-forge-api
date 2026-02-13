package com.voleksiienko.specforgeapi.core.domain.model.config;

import static com.voleksiienko.specforgeapi.core.domain.model.config.BaseConfig.Fields.SortType.ALPHABETICAL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.voleksiienko.specforgeapi.core.domain.exception.ConfigValidationException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

class BaseConfigTest {

    private final BaseConfig.Naming validNaming = new BaseConfig.Naming("ValidName");
    private final BaseConfig.Fields validFields = new BaseConfig.Fields(ALPHABETICAL);

    @Nested
    class RootValidation {

        @Test
        void shouldCreateValidConfig() {
            var config = new BaseConfig(validNaming, validFields);

            assertThat(config.naming()).isEqualTo(validNaming);
            assertThat(config.fields()).isEqualTo(validFields);
        }

        @Test
        void shouldThrowWhenNamingIsNull() {
            assertThatThrownBy(() -> new BaseConfig(null, validFields))
                    .isInstanceOf(ConfigValidationException.class)
                    .hasMessage("Naming configuration is mandatory");
        }

        @Test
        void shouldThrowWhenFieldsIsNull() {
            assertThatThrownBy(() -> new BaseConfig(validNaming, null))
                    .isInstanceOf(ConfigValidationException.class)
                    .hasMessage("Fields configuration is mandatory");
        }
    }

    @Nested
    class NamingValidation {

        @Test
        void shouldAcceptValidNames() {
            assertThat(new BaseConfig.Naming("User").className()).isEqualTo("User");
            assertThat(new BaseConfig.Naming("UserDTO").className()).isEqualTo("UserDTO");
            assertThat(new BaseConfig.Naming("My_Class_1").className()).isEqualTo("My_Class_1");
        }

        @ParameterizedTest
        @NullSource
        @ValueSource(strings = {"", "   "})
        void shouldThrowWhenBlank(String blankName) {
            assertThatThrownBy(() -> new BaseConfig.Naming(blankName))
                    .isInstanceOf(ConfigValidationException.class)
                    .hasMessage("Class name must not be blank");
        }

        @ParameterizedTest
        @ValueSource(strings = {"user", "1User", "_User", "User-Name", "User Name"})
        void shouldThrowWhenPatternDoesNotMatch(String invalidName) {
            assertThatThrownBy(() -> new BaseConfig.Naming(invalidName))
                    .isInstanceOf(ConfigValidationException.class)
                    .hasMessageContaining("must start with an uppercase letter");
        }
    }

    @Nested
    class FieldsValidation {

        @Test
        void shouldCreateWithValidSort() {
            var fields = new BaseConfig.Fields(BaseConfig.Fields.SortType.AS_IS);
            assertThat(fields.sort()).isEqualTo(BaseConfig.Fields.SortType.AS_IS);
        }

        @Test
        void shouldThrowWhenSortIsNull() {
            assertThatThrownBy(() -> new BaseConfig.Fields(null))
                    .isInstanceOf(ConfigValidationException.class)
                    .hasMessage("Field sort type is mandatory");
        }
    }
}
