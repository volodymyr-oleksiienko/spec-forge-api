package com.voleksiienko.specforgeapi.core.application.service.java.inner.type;

import static com.voleksiienko.specforgeapi.core.domain.model.error.DomainErrorCode.SPEC_TO_JAVA_CONVERSION_FAILED;

import com.voleksiienko.specforgeapi.core.application.exception.ConversionException;
import com.voleksiienko.specforgeapi.core.domain.model.java.TypeReference;
import com.voleksiienko.specforgeapi.core.domain.model.spec.type.SpecType;
import java.util.List;

public class JavaTypeReferenceCreatorFacade {

    private final List<JavaTypeReferenceCreator> creators;

    public JavaTypeReferenceCreatorFacade(List<JavaTypeReferenceCreator> creators) {
        this.creators = creators;
    }

    public TypeReference create(String specPropertyName, SpecType specType, JavaMappingContext javaMappingContext) {
        return creators.stream()
                .filter(creator -> creator.supports(specType))
                .findFirst()
                .map(creator -> creator.create(specPropertyName, specType, javaMappingContext))
                .orElseThrow(() -> new ConversionException(
                        "No TypeReferenceCreator found for %s"
                                .formatted(specType.getClass().getName()),
                        SPEC_TO_JAVA_CONVERSION_FAILED));
    }
}
