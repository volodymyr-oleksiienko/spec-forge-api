package com.voleksiienko.specforgeapi.core.domain.model.spec.type;

import com.voleksiienko.specforgeapi.core.common.Asserts;
import com.voleksiienko.specforgeapi.core.domain.exception.SpecModelValidationException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public final class TimeSpecType implements SpecType {

    private final String format;

    private TimeSpecType(Builder builder) {
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

        public TimeSpecType build() {
            if (Asserts.isBlank(format)) {
                throw new SpecModelValidationException("Time format cannot be empty");
            }
            try {
                DateTimeFormatter.ofPattern(format).format(LocalTime.now());
            } catch (Exception e) {
                throw new SpecModelValidationException("Invalid Time format pattern: [%s]".formatted(format), e);
            }
            return new TimeSpecType(this);
        }
    }
}
