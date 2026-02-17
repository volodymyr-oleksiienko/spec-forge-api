package com.voleksiienko.specforgeapi.core.application.service.java.inner;

import com.voleksiienko.specforgeapi.core.application.annotation.Component;
import com.voleksiienko.specforgeapi.core.application.service.java.inner.type.JavaMappingContext;
import com.voleksiienko.specforgeapi.core.application.service.java.inner.type.JavaTypeReferenceCreatorFacade;
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

    private final JavaTypeReferenceCreatorFacade javaTypeReferenceCreatorFacade;
    private final JavaFieldSorter javaFieldSorter;
    private final JavaAnnotationsSupplier javaAnnotationsSupplier;

    public JavaClassFactory(
            JavaTypeReferenceCreatorFacade javaTypeReferenceCreatorFacade,
            JavaFieldSorter javaFieldSorter,
            JavaAnnotationsSupplier javaAnnotationsSupplier) {
        this.javaTypeReferenceCreatorFacade = javaTypeReferenceCreatorFacade;
        this.javaFieldSorter = javaFieldSorter;
        this.javaAnnotationsSupplier = javaAnnotationsSupplier;
    }

    public JavaClass mapToClass(String name, List<SpecProperty> specProperties, JavaMappingContext ctx) {
        List<JavaField> sortedFields = javaFieldSorter.sort(mapToFields(specProperties, ctx), ctx.config());
        boolean isRecord =
                JavaConfig.Structure.Type.RECORD == ctx.config().structure().type();
        List<JavaAnnotation> annotations = generateClassAnnotations(isRecord, sortedFields, ctx.config());
        return isRecord
                ? JavaClass.createRecord(name, annotations, sortedFields)
                : JavaClass.createClass(name, annotations, sortedFields);
    }

    private List<JavaField> mapToFields(List<SpecProperty> specProperties, JavaMappingContext ctx) {
        return specProperties.stream()
                .map(specProperty -> convertPropertyToField(specProperty, ctx))
                .toList();
    }

    private JavaField convertPropertyToField(SpecProperty property, JavaMappingContext ctx) {
        return JavaField.builder()
                .name(property.getName())
                .annotations(buildFieldAnnotations(property, ctx.config()))
                .type(javaTypeReferenceCreatorFacade.create(
                        property.getName(),
                        property.getType(),
                        new JavaMappingContext(ctx, isMustUsePrimitiveWrappers(property, ctx), false)))
                .build();
    }

    private List<JavaAnnotation> buildFieldAnnotations(SpecProperty property, JavaConfig config) {
        List<JavaAnnotation> annotations = new ArrayList<>();
        if (config.validation().enabled()) {
            if (property.isRequired()) {
                if (property.getType() instanceof ListSpecType) {
                    annotations.add(javaAnnotationsSupplier
                            .getAnnotationBuilder("NotEmpty")
                            .build());
                } else if (property.getType() instanceof StringSpecType s
                        && StringSpecType.StringTypeFormat.UUID != s.getFormat()) {
                    annotations.add(javaAnnotationsSupplier
                            .getAnnotationBuilder("NotBlank")
                            .build());

                } else {
                    annotations.add(javaAnnotationsSupplier
                            .getAnnotationBuilder("NotNull")
                            .build());
                }
            }
            if (property.getType().isObjectStructure()) {
                annotations.add(
                        javaAnnotationsSupplier.getAnnotationBuilder("Valid").build());
            }
        }
        if (JavaConfig.Serialization.JsonPropertyMode.ALWAYS
                == config.serialization().jsonPropertyMode()) {
            JavaAnnotation.Builder jsonProp = javaAnnotationsSupplier.getAnnotationBuilder("JsonProperty");
            jsonProp.attributes(Map.of("value", "\"%s\"".formatted(property.getName())));
            annotations.add(jsonProp.build());
        }
        return annotations;
    }

    private boolean isMustUsePrimitiveWrappers(SpecProperty property, JavaMappingContext ctx) {
        return !property.isRequired() || ctx.config().validation().enabled();
    }

    private List<JavaAnnotation> generateClassAnnotations(boolean isRecord, List<JavaField> fields, JavaConfig config) {
        List<JavaAnnotation> annotations = new ArrayList<>();
        if (!isRecord) {
            annotations.add(
                    javaAnnotationsSupplier.getAnnotationBuilder("Getter").build());
            annotations.add(
                    javaAnnotationsSupplier.getAnnotationBuilder("Setter").build());
            annotations.add(javaAnnotationsSupplier
                    .getAnnotationBuilder("NoArgsConstructor")
                    .build());
            annotations.add(javaAnnotationsSupplier
                    .getAnnotationBuilder("AllArgsConstructor")
                    .build());
        }
        if (config.builder().enabled() && (!config.builder().onlyIfMultipleFields() || fields.size() > 1)) {
            annotations.add(
                    javaAnnotationsSupplier.getAnnotationBuilder("Builder").build());
        }
        return annotations;
    }
}
