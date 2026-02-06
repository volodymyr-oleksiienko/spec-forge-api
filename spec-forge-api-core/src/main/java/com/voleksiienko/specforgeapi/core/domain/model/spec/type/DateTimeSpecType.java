package com.voleksiienko.specforgeapi.core.domain.model.spec.type;

import com.voleksiienko.specforgeapi.core.common.Asserts;
import com.voleksiienko.specforgeapi.core.domain.exception.SpecModelValidationException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

public final class DateTimeSpecType implements SpecType {

    private final String format;

    private DateTimeSpecType(Builder builder) {
        this.format = builder.format;
    }

    public static DateTimeSpecType.Builder builder() {
        return new Builder();
    }

    @Override
    public boolean isObjectStructure() {
        return false;
    }

    public String getFormat() {
        return format;
    }

    public static class Builder {

        private String format;

        public Builder format(String format) {
            this.format = format;
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
            return new DateTimeSpecType(this);
        }
    }
}
