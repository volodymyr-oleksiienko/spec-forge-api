package com.voleksiienko.specforgeapi.core.domain.model.spec.type;

import com.voleksiienko.specforgeapi.core.domain.exception.SpecModelValidationException;
import java.util.Objects;
import java.util.regex.Pattern;

public final class StringSpecType implements SpecType {

    private final String pattern;
    private final Integer minLength;
    private final Integer maxLength;
    private final StringTypeFormat format;

    private StringSpecType(Builder builder) {
        this.pattern = builder.pattern;
        this.minLength = builder.minLength;
        this.maxLength = builder.maxLength;
        this.format = builder.format;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public boolean isObjectStructure() {
        return false;
    }

    public String getPattern() {
        return pattern;
    }

    public Integer getMinLength() {
        return minLength;
    }

    public Integer getMaxLength() {
        return maxLength;
    }

    public StringTypeFormat getFormat() {
        return format;
    }

    public enum StringTypeFormat {
        EMAIL,
        UUID,
    }

    public static class Builder {

        private String pattern;
        private Integer minLength;
        private Integer maxLength;
        private StringTypeFormat format;

        public Builder pattern(String pattern) {
            this.pattern = pattern;
            return this;
        }

        public Builder minLength(Integer minLength) {
            this.minLength = minLength;
            return this;
        }

        public Builder maxLength(Integer maxLength) {
            this.maxLength = maxLength;
            return this;
        }

        public Builder format(StringTypeFormat format) {
            this.format = format;
            return this;
        }

        public StringSpecType build() {
            if (Objects.nonNull(minLength) && minLength < 0) {
                throw new SpecModelValidationException("MinLength [%s] cannot be negative".formatted(minLength));
            }
            if (Objects.nonNull(maxLength) && maxLength < 0) {
                throw new SpecModelValidationException("MaxLength [%s] cannot be negative".formatted(maxLength));
            }
            if (Objects.nonNull(minLength) && Objects.nonNull(maxLength) && minLength > maxLength) {
                throw new SpecModelValidationException(
                        "MinLength [%s] cannot be greater than MaxLength [%s]".formatted(minLength, maxLength));
            }
            if (Objects.nonNull(pattern) && Objects.nonNull(format)) {
                throw new SpecModelValidationException("Cannot use both 'pattern' and 'format' together");
            }
            if (Objects.nonNull(pattern)) {
                try {
                    Pattern.compile(pattern);
                } catch (Exception e) {
                    throw new SpecModelValidationException(
                            "Invalid Regex Pattern provided: [%s]".formatted(pattern), e);
                }
            }
            return new StringSpecType(this);
        }
    }
}
