package com.voleksiienko.specforgeapi.core.application.port.in.artifact.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

import com.voleksiienko.specforgeapi.core.domain.exception.SpecModelValidationException;
import com.voleksiienko.specforgeapi.core.domain.model.config.JavaConfig;
import com.voleksiienko.specforgeapi.core.domain.model.spec.SpecModel;
import org.junit.jupiter.api.Test;

public class GenerateFromSpecModelCommandTest {

    @Test
    void shouldCreateCommandWithValidSpecModel() {
        SpecModel specModel = mock(SpecModel.class);
        JavaConfig config = mock(JavaConfig.class);

        GenerateFromSpecModelCommand command = new GenerateFromSpecModelCommand(specModel, config);
        assertThat(command.specModel()).isEqualTo(specModel);
        assertThat(command.config()).isEqualTo(config);
    }

    @Test
    void shouldThrowWhenJsonSchemaIsInvalid() {
        JavaConfig config = mock(JavaConfig.class);

        assertThatThrownBy(() -> new GenerateFromSpecModelCommand(null, config))
                .isInstanceOf(SpecModelValidationException.class)
                .hasMessage("specModel cannot be null");
    }
}
