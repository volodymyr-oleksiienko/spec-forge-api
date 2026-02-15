package com.voleksiienko.specforgeapi.core.domain.model.ts;

import com.voleksiienko.specforgeapi.core.common.Asserts;
import com.voleksiienko.specforgeapi.core.domain.exception.TsModelValidationException;
import java.util.List;

public final class TsEnum implements TsDeclaration {

    private final String name;
    private final List<TsEnumConstant> constants;

    private TsEnum(Builder builder) {
        this.name = builder.name;
        this.constants = builder.constants;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public String getName() {
        return name;
    }

    public List<TsEnumConstant> getConstants() {
        return constants;
    }

    public static class Builder {

        private String name;
        private List<TsEnumConstant> constants;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder constants(List<TsEnumConstant> constants) {
            this.constants = constants;
            return this;
        }

        public TsEnum build() {
            if (Asserts.isBlank(name)) {
                throw new TsModelValidationException("TsEnum name cannot be blank");
            }
            if (Asserts.isEmpty(constants)) {
                throw new TsModelValidationException("TsEnum must have at least one constant");
            }
            this.constants = List.copyOf(constants);
            return new TsEnum(this);
        }
    }
}
