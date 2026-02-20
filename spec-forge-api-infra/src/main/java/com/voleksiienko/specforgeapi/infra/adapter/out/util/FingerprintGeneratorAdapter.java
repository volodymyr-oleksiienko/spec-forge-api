package com.voleksiienko.specforgeapi.infra.adapter.out.util;

import static com.voleksiienko.specforgeapi.core.domain.model.error.DomainErrorCode.SPEC_TO_JAVA_CONVERSION_FAILED;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.voleksiienko.specforgeapi.core.application.exception.ConversionException;
import com.voleksiienko.specforgeapi.core.application.port.out.util.FingerprintGeneratorPort;
import com.voleksiienko.specforgeapi.core.domain.model.java.JavaType;
import com.voleksiienko.specforgeapi.core.domain.model.ts.TsDeclaration;
import org.springframework.stereotype.Component;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.json.JsonMapper;

@Component
public class FingerprintGeneratorAdapter implements FingerprintGeneratorPort {

    private static final JsonMapper JSON_MAPPER = JsonMapper.builder()
            .enable(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS)
            .addMixIn(JavaType.class, IgnoreNameMixin.class)
            .addMixIn(TsDeclaration.class, IgnoreNameMixin.class)
            .build();

    @Override
    public String map(Object type) {
        try {
            return JSON_MAPPER.writeValueAsString(type);
        } catch (Exception e) {
            throw new ConversionException("Object hashing failed", e, SPEC_TO_JAVA_CONVERSION_FAILED);
        }
    }

    private abstract static class IgnoreNameMixin {
        @JsonIgnore
        abstract String getName();
    }
}
