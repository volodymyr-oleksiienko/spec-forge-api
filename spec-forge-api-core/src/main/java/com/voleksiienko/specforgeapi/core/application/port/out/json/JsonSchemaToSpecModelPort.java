package com.voleksiienko.specforgeapi.core.application.port.out.json;

import com.voleksiienko.specforgeapi.core.domain.model.conversion.ConversionResult;

public interface JsonSchemaToSpecModelPort {

    ConversionResult map(String jsonSchema);
}
