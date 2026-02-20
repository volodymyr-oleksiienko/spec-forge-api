package com.voleksiienko.specforgeapi.core.application.service.ts.inner;

import com.voleksiienko.specforgeapi.core.application.annotation.Component;
import com.voleksiienko.specforgeapi.core.domain.model.config.TypeScriptConfig;
import com.voleksiienko.specforgeapi.core.domain.model.ts.TsField;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

@Component
public class TsFieldSorter {

    private static final Set<String> TS_PRIMITIVES = Set.of("string", "number", "boolean");

    public List<TsField> sort(List<TsField> fields, TypeScriptConfig config) {
        List<TsField> sortedFields = new ArrayList<>(fields);
        switch (config.base().fields().sort()) {
            case ALPHABETICAL -> sortedFields.sort(Comparator.comparing(TsField::getName));
            case REQUIRED_FIRST -> sortedFields.sort(Comparator.comparingInt(this::getFieldWeight));
            case AS_IS -> {
                // natural sorting used
            }
        }
        return sortedFields;
    }

    /**
     * Calculates the sorting weight of the field.
     * 1 - Required Primitives (highest priority)
     * 2 - Required Objects/Other
     * 3 - Optional fields (lowest priority)
     */
    private int getFieldWeight(TsField field) {
        if (field.isOptional()) {
            return 3;
        }
        if (isPrimitive(field)) {
            return 1;
        }
        return 2;
    }

    private boolean isPrimitive(TsField field) {
        return TS_PRIMITIVES.contains(field.getType().getTypeName());
    }
}
