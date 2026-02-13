package com.voleksiienko.specforgeapi.core.application.service.java.inner;

import com.voleksiienko.specforgeapi.core.application.annotation.Component;
import com.voleksiienko.specforgeapi.core.application.service.java.inner.type.MappingContext;
import com.voleksiienko.specforgeapi.core.application.service.java.inner.type.TypeReferenceCreatorFacade;
import com.voleksiienko.specforgeapi.core.domain.model.config.JavaConfig;
import com.voleksiienko.specforgeapi.core.domain.model.java.JavaAnnotation;
import com.voleksiienko.specforgeapi.core.domain.model.java.JavaClass;
import com.voleksiienko.specforgeapi.core.domain.model.java.JavaField;
import com.voleksiienko.specforgeapi.core.domain.model.spec.SpecProperty;
import com.voleksiienko.specforgeapi.core.domain.model.spec.type.ListSpecType;
import com.voleksiienko.specforgeapi.core.domain.model.spec.type.StringSpecType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class JavaClassFactory {

    private final TypeReferenceCreatorFacade typeReferenceCreatorFacade;
    private final JavaFieldSorter javaFieldSorter;
    private final AnnotationsSupplier annotationsSupplier;

    public JavaClassFactory(
            TypeReferenceCreatorFacade typeReferenceCreatorFacade,
            JavaFieldSorter javaFieldSorter,
            AnnotationsSupplier annotationsSupplier) {
        this.typeReferenceCreatorFacade = typeReferenceCreatorFacade;
        this.javaFieldSorter = javaFieldSorter;
        this.annotationsSupplier = annotationsSupplier;
    }

    public JavaClass mapToClass(String name, List<SpecProperty> specProperties, MappingContext ctx) {
        List<JavaField> sortedFields = javaFieldSorter.sort(mapToFields(specProperties, ctx), ctx.config());
        boolean isRecord =
                JavaConfig.Structure.Type.RECORD == ctx.config().structure().type();
        List<JavaAnnotation> annotations = generateClassAnnotations(isRecord, sortedFields, ctx.config());
        return isRecord
                ? JavaClass.createRecord(name, annotations, sortedFields)
                : JavaClass.createClass(name, annotations, sortedFields);
    }

    private List<JavaField> mapToFields(List<SpecProperty> specProperties, MappingContext ctx) {
        return specProperties.stream()
                .map(specProperty -> convertPropertyToField(specProperty, ctx))
                .toList();
    }

    private JavaField convertPropertyToField(SpecProperty property, MappingContext ctx) {
        return JavaField.builder()
                .name(property.getName())
                .annotations(buildFieldAnnotations(property, ctx.config()))
                .type(typeReferenceCreatorFacade.create(
                        property.getName(),
                        property.getType(),
                        new MappingContext(ctx, isMustUsePrimitiveWrappers(property, ctx), false)))
                .build();
    }

    private List<JavaAnnotation> buildFieldAnnotations(SpecProperty property, JavaConfig config) {
        List<JavaAnnotation> annotations = new ArrayList<>();
        if (config.validation().enabled()) {
            if (property.isRequired()) {
                if (property.getType() instanceof ListSpecType) {
                    annotations.add(
                            annotationsSupplier.getAnnotationBuilder("NotEmpty").build());
                } else if (property.getType() instanceof StringSpecType s
                        && StringSpecType.StringTypeFormat.UUID != s.getFormat()) {
                    annotations.add(
                            annotationsSupplier.getAnnotationBuilder("NotBlank").build());

                } else {
                    annotations.add(
                            annotationsSupplier.getAnnotationBuilder("NotNull").build());
                }
            }
            if (property.getType().isObjectStructure()) {
                annotations.add(
                        annotationsSupplier.getAnnotationBuilder("Valid").build());
            }
        }
        if (JavaConfig.Serialization.JsonPropertyMode.ALWAYS
                == config.serialization().jsonPropertyMode()) {
            JavaAnnotation.Builder jsonProp = annotationsSupplier.getAnnotationBuilder("JsonProperty");
            jsonProp.attributes(Map.of("value", "\"%s\"".formatted(property.getName())));
            annotations.add(jsonProp.build());
        }
        return annotations;
    }

    private boolean isMustUsePrimitiveWrappers(SpecProperty property, MappingContext ctx) {
        return !property.isRequired() || ctx.config().validation().enabled();
    }

    private List<JavaAnnotation> generateClassAnnotations(boolean isRecord, List<JavaField> fields, JavaConfig config) {
        List<JavaAnnotation> annotations = new ArrayList<>();
        if (!isRecord) {
            annotations.add(annotationsSupplier.getAnnotationBuilder("Getter").build());
            annotations.add(annotationsSupplier.getAnnotationBuilder("Setter").build());
            annotations.add(annotationsSupplier
                    .getAnnotationBuilder("NoArgsConstructor")
                    .build());
            annotations.add(annotationsSupplier
                    .getAnnotationBuilder("AllArgsConstructor")
                    .build());
        }
        if (config.builder().enabled() && (!config.builder().onlyIfMultipleFields() || fields.size() > 1)) {
            annotations.add(annotationsSupplier.getAnnotationBuilder("Builder").build());
        }
        return annotations;
    }
}
