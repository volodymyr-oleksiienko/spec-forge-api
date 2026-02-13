package com.voleksiienko.specforgeapi.core.application.service.java.inner.type;

import static com.voleksiienko.specforgeapi.core.domain.model.error.DomainErrorCode.SPEC_TO_JAVA_CONVERSION_FAILED;

import com.voleksiienko.specforgeapi.core.application.annotation.Component;
import com.voleksiienko.specforgeapi.core.application.exception.ConversionException;
import com.voleksiienko.specforgeapi.core.domain.model.java.TypeReference;
import com.voleksiienko.specforgeapi.core.domain.model.spec.type.SpecType;
import java.util.List;

@Component
public class TypeReferenceCreatorFacade {

    private final List<TypeReferenceCreator> creators;

    public TypeReferenceCreatorFacade(List<TypeReferenceCreator> creators) {
        this.creators = creators;
    }

    public TypeReference create(String specPropertyName, SpecType specType, MappingContext mappingContext) {
        return creators.stream()
                .filter(creator -> creator.supports(specType))
                .findFirst()
                .map(creator -> creator.create(specPropertyName, specType, mappingContext))
                .orElseThrow(() -> new ConversionException(
                        "No TypeReferenceCreator found for %s"
                                .formatted(specType.getClass().getName()),
                        SPEC_TO_JAVA_CONVERSION_FAILED));
    }
}
