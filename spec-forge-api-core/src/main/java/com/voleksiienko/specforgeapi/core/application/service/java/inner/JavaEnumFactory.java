package com.voleksiienko.specforgeapi.core.application.service.java.inner;

import com.voleksiienko.specforgeapi.core.application.annotation.Component;
import com.voleksiienko.specforgeapi.core.common.Asserts;
import com.voleksiienko.specforgeapi.core.domain.model.java.*;
import com.voleksiienko.specforgeapi.core.domain.model.spec.type.EnumSpecType;
import java.util.ArrayList;
import java.util.List;

@Component
public class JavaEnumFactory {

    private final JavaAnnotationsSupplier javaAnnotationsSupplier;

    public JavaEnumFactory(JavaAnnotationsSupplier javaAnnotationsSupplier) {
        this.javaAnnotationsSupplier = javaAnnotationsSupplier;
    }

    public JavaEnum convertToEnum(String enumName, EnumSpecType type) {
        boolean isNumeric = type.isNumeric();
        List<ConstantDefinition> definitions = createDefinitions(type, isNumeric);
        boolean needsField = isNumeric || definitions.stream().anyMatch(d -> !d.name.equals(d.originalValue));
        return buildJavaEnum(enumName, definitions, needsField, isNumeric);
    }

    private List<ConstantDefinition> createDefinitions(EnumSpecType type, boolean isNumeric) {
        return type.getValues().stream()
                .map(val -> new ConstantDefinition(sanitizeName(val, isNumeric), val))
                .toList();
    }

    private String sanitizeName(String value, boolean isNumeric) {
        if (isNumeric) {
            return "VALUE_" + value;
        }
        String safeName = value.replaceAll("[^a-zA-Z0-9_]", "_").toUpperCase();
        if (Asserts.isNotBlank(safeName) && Character.isDigit(safeName.charAt(0))) {
            return "_" + safeName;
        }
        return Asserts.isBlank(safeName) ? "UNKNOWN" : safeName;
    }

    private JavaEnum buildJavaEnum(
            String enumName, List<ConstantDefinition> definitions, boolean needsField, boolean isNumeric) {
        List<JavaEnumConstant> constants = definitions.stream()
                .map(def -> buildConstant(def, needsField, isNumeric))
                .toList();

        List<JavaField> fields = new ArrayList<>();
        List<JavaAnnotation> annotations = new ArrayList<>();

        if (needsField) {
            fields.add(createValueField(isNumeric));
            annotations.add(
                    javaAnnotationsSupplier.getAnnotationBuilder("Getter").build());
            annotations.add(javaAnnotationsSupplier
                    .getAnnotationBuilder("RequiredArgsConstructor")
                    .build());
        }

        return JavaEnum.of(enumName, annotations, fields, constants);
    }

    private JavaEnumConstant buildConstant(ConstantDefinition def, boolean needsField, boolean isNumeric) {
        List<String> arguments = List.of();
        if (needsField) {
            arguments = List.of(isNumeric ? def.originalValue + "L" : "\"" + def.originalValue + "\"");
        }
        return JavaEnumConstant.builder().name(def.name).arguments(arguments).build();
    }

    private JavaField createValueField(boolean isNumeric) {
        return JavaField.builder()
                .name("value")
                .type(TypeReference.builder()
                        .packageName(isNumeric ? null : "java.lang")
                        .simpleName(isNumeric ? "long" : "String")
                        .primitive(isNumeric)
                        .build())
                .build();
    }

    private record ConstantDefinition(String name, String originalValue) {}
}
