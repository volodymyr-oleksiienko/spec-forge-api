package com.voleksiienko.specforgeapi.core.domain.model.spec.type;

import com.voleksiienko.specforgeapi.core.common.Asserts;
import com.voleksiienko.specforgeapi.core.domain.exception.SpecModelValidationException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public final class DateTimeSpecType extends PrimitiveSpecType {

    private final String format;

    private DateTimeSpecType(Builder builder) {
        super(builder.examples);
        this.format = builder.format;
    }

    public static DateTimeSpecType.Builder builder() {
        return new Builder();
    }

    public String getFormat() {
        return format;
    }

    public static class Builder {

        private String format;
        private List<String> examples;

        public Builder format(String format) {
            this.format = format;
            return this;
        }

        public Builder examples(List<String> examples) {
            this.examples = examples;
            return this;
        }

        public DateTimeSpecType build() {
            if (Asserts.isBlank(format)) {
                throw new SpecModelValidationException("DateTime format cannot be empty");
            }
            try {
                DateTimeFormatter.ofPattern(format).format(OffsetDateTime.now());
            } catch (Exception e) {
                throw new SpecModelValidationException("Invalid DateTime format pattern: [%s]".formatted(format), e);
            }
            if (Asserts.isNotEmpty(examples)) {
                examples = List.copyOf(examples);
            }
            return new DateTimeSpecType(this);
        }
    }
}
