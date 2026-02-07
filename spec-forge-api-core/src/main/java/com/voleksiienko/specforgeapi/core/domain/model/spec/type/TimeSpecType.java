package com.voleksiienko.specforgeapi.core.domain.model.spec.type;

import com.voleksiienko.specforgeapi.core.common.Asserts;
import com.voleksiienko.specforgeapi.core.domain.exception.SpecModelValidationException;
import java.time.LocalTime;
import java.time.OffsetTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public final class TimeSpecType extends PrimitiveSpecType {

    private final String format;

    private TimeSpecType(Builder builder) {
        super(builder.examples);
        this.format = builder.format;
    }

    public static Builder builder() {
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

        public TimeSpecType build() {
            if (Asserts.isBlank(format)) {
                throw new SpecModelValidationException("Time format cannot be empty");
            }
            DateTimeFormatter formatter = getFormatter();
            examples = List.of(generatedExample(formatter));
            return new TimeSpecType(this);
        }

        private DateTimeFormatter getFormatter() {
            DateTimeFormatter dateTimeFormatter;
            try {
                dateTimeFormatter = DateTimeFormatter.ofPattern(format);
            } catch (IllegalArgumentException e) {
                throw new SpecModelValidationException("Invalid Time format syntax: [%s]".formatted(format), e);
            }

            try {
                dateTimeFormatter.format(OffsetTime.now());
                return dateTimeFormatter;
            } catch (Exception ignored) {
                // ignore to try format with LocalTime
            }

            try {
                dateTimeFormatter.format(LocalTime.now());
                return dateTimeFormatter;
            } catch (Exception e) {
                throw new SpecModelValidationException(
                        "Format [%s] is not compatible with supported Time types".formatted(format), e);
            }
        }

        private String generatedExample(DateTimeFormatter dateTimeFormatter) {
            try {
                return dateTimeFormatter.format(OffsetTime.now());
            } catch (Exception _) {
                try {
                    return dateTimeFormatter.format(LocalTime.now());
                } catch (Exception e2) {
                    throw new SpecModelValidationException(
                            "Format [%s] is not compatible with supported Time types".formatted(format), e2);
                }
            }
        }
    }
}
