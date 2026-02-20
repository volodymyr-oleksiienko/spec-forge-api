package com.voleksiienko.specforgeapi.core.domain.model.spec.type;

import com.voleksiienko.specforgeapi.core.common.Asserts;
import com.voleksiienko.specforgeapi.core.domain.exception.SpecModelValidationException;
import java.time.LocalDateTime;
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

        public DateTimeSpecType build() {
            if (Asserts.isBlank(format)) {
                throw new SpecModelValidationException("DateTime format cannot be empty");
            }
            DateTimeFormatter formatter = getFormatter();
            examples = List.of(generateExample(formatter));
            return new DateTimeSpecType(this);
        }

        private DateTimeFormatter getFormatter() {
            DateTimeFormatter dateTimeFormatter;
            try {
                dateTimeFormatter = DateTimeFormatter.ofPattern(format);
            } catch (IllegalArgumentException e) {
                throw new SpecModelValidationException(
                        "Invalid DateTime format pattern syntax: [%s]".formatted(format), e);
            }

            try {
                dateTimeFormatter.format(OffsetDateTime.now());
                return dateTimeFormatter;
            } catch (Exception ignored) {
                // ignore to try format with LocalDateTime
            }

            try {
                dateTimeFormatter.format(LocalDateTime.now());
                return dateTimeFormatter;
            } catch (Exception e) {
                throw new SpecModelValidationException(
                        "Format [%s] is not compatible with supported DateTime types".formatted(format), e);
            }
        }

        private String generateExample(DateTimeFormatter dateTimeFormatter) {
            try {
                return dateTimeFormatter.format(OffsetDateTime.now());
            } catch (Exception _) {
                try {
                    return dateTimeFormatter.format(LocalDateTime.now());
                } catch (Exception e2) {
                    throw new SpecModelValidationException(
                            "Format [%s] is not compatible with supported DateTime types".formatted(format), e2);
                }
            }
        }
    }
}
