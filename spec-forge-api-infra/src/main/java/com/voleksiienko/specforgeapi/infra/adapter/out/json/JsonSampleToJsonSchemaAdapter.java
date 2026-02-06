package com.voleksiienko.specforgeapi.infra.adapter.out.json;

import static com.voleksiienko.specforgeapi.core.domain.model.error.JsonMappingErrorCode.JSON_SAMPLE_PARSING_FAILED;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.saasquatch.jsonschemainferrer.FormatInferrers;
import com.saasquatch.jsonschemainferrer.JsonSchemaInferrer;
import com.saasquatch.jsonschemainferrer.SpecVersion;
import com.voleksiienko.specforgeapi.core.application.exception.ConversionException;
import com.voleksiienko.specforgeapi.core.application.port.out.json.JsonSampleToJsonSchemaPort;
import org.springframework.stereotype.Service;

@Service
public class JsonSampleToJsonSchemaAdapter implements JsonSampleToJsonSchemaPort {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final JsonSchemaInferrer JSON_SCHEMA_INFERRER = JsonSchemaInferrer.newBuilder()
            .setSpecVersion(SpecVersion.DRAFT_2020_12)
            .addFormatInferrers(FormatInferrers.dateTime(), FormatInferrers.email())
            .build();

    public String map(String jsonSample) {
        try {
            return JSON_SCHEMA_INFERRER
                    .inferForSample(OBJECT_MAPPER.readTree(jsonSample))
                    .toPrettyString();
        } catch (Exception e) {
            throw new ConversionException(
                    "Failed to convert json sample to json schema", e, JSON_SAMPLE_PARSING_FAILED);
        }
    }
}
