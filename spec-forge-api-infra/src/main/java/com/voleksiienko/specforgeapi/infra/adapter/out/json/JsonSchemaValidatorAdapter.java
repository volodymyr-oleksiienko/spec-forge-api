package com.voleksiienko.specforgeapi.infra.adapter.out.json;

import static com.voleksiienko.specforgeapi.core.domain.model.error.JsonMappingErrorCode.JSON_SCHEMA_VALIDATION_FAILED;

import com.networknt.schema.*;
import com.networknt.schema.Error;
import com.networknt.schema.dialect.Dialects;
import com.voleksiienko.specforgeapi.core.application.exception.ConversionException;
import com.voleksiienko.specforgeapi.core.application.port.out.json.JsonSchemaValidatorPort;
import java.util.List;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

@Service
public class JsonSchemaValidatorAdapter implements JsonSchemaValidatorPort {

    private final Schema schema;

    public JsonSchemaValidatorAdapter() {
        SchemaRegistry schemaRegistry = SchemaRegistry.withDialect(Dialects.getDraft202012());
        this.schema = schemaRegistry.getSchema(
                SchemaLocation.of(Dialects.getDraft202012().getId()));
    }

    @Override
    public void validate(String jsonSchema) {
        List<Error> errors;
        try {
            errors = schema.validate(
                    jsonSchema,
                    InputFormat.JSON,
                    executionContext -> executionContext.executionConfig(
                            executionConfig -> executionConfig.formatAssertionsEnabled(true)));
        } catch (Exception e) {
            throw new ConversionException("Json schema contains syntax errors", e, JSON_SCHEMA_VALIDATION_FAILED);
        }
        if (CollectionUtils.isNotEmpty(errors)) {
            throw new ConversionException("Json schema is invalid", JSON_SCHEMA_VALIDATION_FAILED);
        }
    }
}
