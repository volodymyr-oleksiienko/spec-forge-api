package com.voleksiienko.specforgeapi.core.application.service.java.inner;

import com.voleksiienko.specforgeapi.core.application.annotation.Component;
import com.voleksiienko.specforgeapi.core.domain.model.java.JavaAnnotation;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

@Component
public class AnnotationsSupplier {

    public static final String LOMBOK_PACKAGE = "lombok";
    private static final Map<String, Supplier<JavaAnnotation.Builder>> ANNOTATION_SUPPLIERS = new HashMap<>();
    private static final String JAKARTA_VALIDATION_CONSTRAINTS_PACKAGE = "jakarta.validation.constraints";

    static {
        ANNOTATION_SUPPLIERS.put("JsonProperty", () -> JavaAnnotation.builder()
                .packageName("com.fasterxml.jackson.annotation")
                .simpleName("JsonProperty"));
        ANNOTATION_SUPPLIERS.put("NotNull", () -> JavaAnnotation.builder()
                .packageName(JAKARTA_VALIDATION_CONSTRAINTS_PACKAGE)
                .simpleName("NotNull"));
        ANNOTATION_SUPPLIERS.put("NotBlank", () -> JavaAnnotation.builder()
                .packageName(JAKARTA_VALIDATION_CONSTRAINTS_PACKAGE)
                .simpleName("NotBlank"));
        ANNOTATION_SUPPLIERS.put("NotEmpty", () -> JavaAnnotation.builder()
                .packageName(JAKARTA_VALIDATION_CONSTRAINTS_PACKAGE)
                .simpleName("NotEmpty"));
        ANNOTATION_SUPPLIERS.put(
                "Valid",
                () -> JavaAnnotation.builder().packageName("jakarta.validation").simpleName("Valid"));

        ANNOTATION_SUPPLIERS.put(
                "Getter",
                () -> JavaAnnotation.builder().packageName(LOMBOK_PACKAGE).simpleName("Getter"));
        ANNOTATION_SUPPLIERS.put(
                "Setter",
                () -> JavaAnnotation.builder().packageName(LOMBOK_PACKAGE).simpleName("Setter"));
        ANNOTATION_SUPPLIERS.put(
                "Builder",
                () -> JavaAnnotation.builder().packageName(LOMBOK_PACKAGE).simpleName("Builder"));
        ANNOTATION_SUPPLIERS.put(
                "NoArgsConstructor",
                () -> JavaAnnotation.builder().packageName(LOMBOK_PACKAGE).simpleName("NoArgsConstructor"));
        ANNOTATION_SUPPLIERS.put(
                "AllArgsConstructor",
                () -> JavaAnnotation.builder().packageName(LOMBOK_PACKAGE).simpleName("AllArgsConstructor"));
    }

    public JavaAnnotation.Builder getAnnotationBuilder(String annotationName) {
        return ANNOTATION_SUPPLIERS.get(annotationName).get();
    }
}
