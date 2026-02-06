package com.voleksiienko.specforgeapi.core.domain.model.spec.type;

import com.voleksiienko.specforgeapi.core.common.Asserts;
import com.voleksiienko.specforgeapi.core.domain.exception.SpecModelValidationException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public final class DateSpecType implements SpecType {

    private final String format;

    private DateSpecType(Builder builder) {
        this.format = builder.format;
    }

    public static Builder builder() {
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

        public DateSpecType build() {
            if (Asserts.isBlank(format)) {
                throw new SpecModelValidationException("Date format cannot be empty");
            }
            try {
                DateTimeFormatter.ofPattern(format).format(LocalDate.now());
            } catch (Exception e) {
                throw new SpecModelValidationException("Invalid Date format pattern: %s".formatted(format), e);
            }
            return new DateSpecType(this);
        }
    }
}
