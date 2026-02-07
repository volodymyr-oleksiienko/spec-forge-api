package com.voleksiienko.specforgeapi.core.domain.model.spec.type;

import com.voleksiienko.specforgeapi.core.common.Asserts;
import com.voleksiienko.specforgeapi.core.domain.exception.SpecModelValidationException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Pattern;

public final class StringSpecType extends PrimitiveSpecType {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    private final String pattern;
    private final Integer minLength;
    private final Integer maxLength;
    private final StringTypeFormat format;

    private StringSpecType(Builder builder) {
        super(builder.examples);
        this.pattern = builder.pattern;
        this.minLength = builder.minLength;
        this.maxLength = builder.maxLength;
        this.format = builder.format;
    }

    public static Builder builder() {
        return new Builder();
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
        private List<String> examples;

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

        public Builder examples(List<String> examples) {
            this.examples = examples;
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
            Pattern regexpPattern = validatePattern();
            if (Asserts.isNotEmpty(examples)) {
                validateExamples(regexpPattern);
                examples = List.copyOf(examples);
            }
            return new StringSpecType(this);
        }

        private Pattern validatePattern() {
            Pattern regexpPattern = null;
            if (Objects.nonNull(pattern)) {
                try {
                    regexpPattern = Pattern.compile(pattern);
                } catch (Exception e) {
                    throw new SpecModelValidationException(
                            "Invalid Regex Pattern provided: [%s]".formatted(pattern), e);
                }
            }
            return regexpPattern;
        }

        private void validateExamples(Pattern customRegex) {
            for (String example : examples) {
                validateByLength(example);
                validateByRegexp(customRegex, example);
                validateByFormat(example);
            }
        }

        private void validateByLength(String example) {
            if (Objects.nonNull(minLength) && example.length() < minLength) {
                throw new SpecModelValidationException("Example [%s] length [%s] is shorter than minLength [%s]"
                        .formatted(example, example.length(), minLength));
            }
            if (Objects.nonNull(maxLength) && example.length() > maxLength) {
                throw new SpecModelValidationException("Example [%s] length [%s] is longer than maxLength [%s]"
                        .formatted(example, example.length(), maxLength));
            }
        }

        private void validateByRegexp(Pattern customRegex, String example) {
            if (Objects.nonNull(customRegex) && !customRegex.matcher(example).matches()) {
                throw new SpecModelValidationException(
                        "Example [%s] does not match pattern [%s]".formatted(example, pattern));
            }
        }

        private void validateByFormat(String example) {
            if (StringTypeFormat.UUID == format) {
                try {
                    UUID.fromString(example);
                } catch (IllegalArgumentException e) {
                    throw new SpecModelValidationException("Example [%s] is not a valid UUID".formatted(example));
                }
            } else if (StringTypeFormat.EMAIL == format) {
                if (!EMAIL_PATTERN.matcher(example).matches()) {
                    throw new SpecModelValidationException("Example [%s] is not a valid EMAIL".formatted(example));
                }
            }
        }
    }
}
