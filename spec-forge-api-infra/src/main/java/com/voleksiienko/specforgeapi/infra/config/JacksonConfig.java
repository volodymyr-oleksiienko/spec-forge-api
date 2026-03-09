package com.voleksiienko.specforgeapi.infra.config;

import com.voleksiienko.specforgeapi.core.common.Asserts;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.deser.jdk.StringDeserializer;
import tools.jackson.databind.deser.std.StdDeserializer;
import tools.jackson.databind.module.SimpleModule;

@Configuration
public class JacksonConfig {

    @Bean
    public SimpleModule emptyStringAsNullDeserializerModule() {
        return new SimpleModule().addDeserializer(String.class, new StdDeserializer<>(String.class) {
            @Override
            public String deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) {
                String string = StringDeserializer.instance.deserialize(jsonParser, deserializationContext);
                return Asserts.isBlank(string) ? null : string;
            }
        });
    }
}
