package com.voleksiienko.specforgeapi.infra.adapter.out.json.inner;

import com.voleksiienko.specforgeapi.core.domain.model.conversion.Warning;
import com.voleksiienko.specforgeapi.core.domain.model.error.ErrorCode;
import java.util.ArrayList;
import java.util.List;

public class ParsingContext {

    private final List<Warning> warnings = new ArrayList<>();

    public void addWarning(String devMessage, ErrorCode code) {
        warnings.add(new Warning(devMessage, code));
    }

    public List<Warning> getWarnings() {
        return List.copyOf(warnings);
    }
}
