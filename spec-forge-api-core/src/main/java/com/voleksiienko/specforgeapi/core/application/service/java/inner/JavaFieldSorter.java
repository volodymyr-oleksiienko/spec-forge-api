package com.voleksiienko.specforgeapi.core.application.service.java.inner;

import com.voleksiienko.specforgeapi.core.application.annotation.Component;
import com.voleksiienko.specforgeapi.core.common.Asserts;
import com.voleksiienko.specforgeapi.core.domain.model.config.JavaConfig;
import com.voleksiienko.specforgeapi.core.domain.model.java.JavaField;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

@Component
public class JavaFieldSorter {

    private static final Set<String> REQUIRED_ANNOTATIONS = Set.of("NotNull", "NotBlank", "NotEmpty", "NonNull");

    public List<JavaField> sort(List<JavaField> fields, JavaConfig config) {
        List<JavaField> sortedFields = new ArrayList<>(fields);
        switch (config.base().fields().sort()) {
            case ALPHABETICAL -> sortedFields.sort(Comparator.comparing(JavaField::getName));
            case REQUIRED_FIRST -> sortedFields.sort(Comparator.comparingInt(this::getFieldWeight));
            case AS_IS -> {}
        }
        return sortedFields;
    }

    /**
     * Calculates the sorting weight of the field.
     * 1 - Primitives (highest priority)
     * 2 - Fields with mandatory annotations
     * 3 - Standard nullable fields (lowest priority)
     */
    private int getFieldWeight(JavaField field) {
        if (field.getType().isPrimitive()) {
            return 1;
        }
        if (hasRequiredAnnotation(field)) {
            return 2;
        }
        return 3;
    }

    private boolean hasRequiredAnnotation(JavaField field) {
        return !Asserts.isEmpty(field.getAnnotations())
                && field.getAnnotations().stream().anyMatch(a -> REQUIRED_ANNOTATIONS.contains(a.getSimpleName()));
    }
}
