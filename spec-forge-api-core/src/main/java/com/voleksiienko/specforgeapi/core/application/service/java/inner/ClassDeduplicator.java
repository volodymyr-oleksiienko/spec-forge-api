package com.voleksiienko.specforgeapi.core.application.service.java.inner;

import com.voleksiienko.specforgeapi.core.application.annotation.Component;
import com.voleksiienko.specforgeapi.core.application.port.out.util.FingerprintGeneratorPort;
import com.voleksiienko.specforgeapi.core.common.Asserts;
import com.voleksiienko.specforgeapi.core.domain.model.java.*;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class ClassDeduplicator {

    private final FingerprintGeneratorPort fingerprintGenerator;

    public ClassDeduplicator(FingerprintGeneratorPort fingerprintGenerator) {
        this.fingerprintGenerator = fingerprintGenerator;
    }

    /**
     * Identifies duplicate JavaTypes (same structure but different names) and removes them.
     * Rewrites references in the remaining classes to point to the canonical versions.
     */
    public List<JavaType> deduplicate(List<JavaType> candidates) {
        // Map: Structural Signature -> Canonical JavaType
        Map<String, JavaType> uniqueTypes = new LinkedHashMap<>();
        // Map: Duplicate Name -> Canonical Name
        Map<String, String> remapping = new HashMap<>();

        for (JavaType candidate : candidates) {
            String signature = fingerprintGenerator.map(candidate);

            if (uniqueTypes.containsKey(signature)) {
                // Duplicate found
                JavaType canonical = uniqueTypes.get(signature);
                remapping.put(candidate.getName(), canonical.getName());
            } else {
                // Unique structure found
                uniqueTypes.put(signature, candidate);
            }
        }

        if (remapping.isEmpty()) {
            return new ArrayList<>(uniqueTypes.values());
        }

        // Rewrite field references in the surviving types to use the canonical names
        return uniqueTypes.values().stream()
                .map(type -> replaceReferences(type, remapping))
                .collect(Collectors.toList());
    }

    private JavaType replaceReferences(JavaType type, Map<String, String> remapping) {
        if (type.getFields().stream().noneMatch(f -> needsRewriting(f.getType(), remapping))) {
            return type;
        }

        List<JavaField> updatedFields = type.getFields().stream()
                .map(field -> updateField(field, remapping))
                .toList();

        return switch (type) {
            case JavaClass javaClass ->
                javaClass.isRecord()
                        ? JavaClass.createRecord(javaClass.getName(), javaClass.getAnnotations(), updatedFields)
                        : JavaClass.createClass(javaClass.getName(), javaClass.getAnnotations(), updatedFields);
            case JavaEnum javaEnum ->
                JavaEnum.of(javaEnum.getName(), javaEnum.getAnnotations(), updatedFields, javaEnum.getConstants());
        };
    }

    private JavaField updateField(JavaField field, Map<String, String> remapping) {
        TypeReference updatedType = updateTypeReference(field.getType(), remapping);
        return JavaField.builder()
                .name(field.getName())
                .type(updatedType)
                .annotations(field.getAnnotations())
                .build();
    }

    private TypeReference updateTypeReference(TypeReference typeRef, Map<String, String> remapping) {

        String newName = remapping.getOrDefault(typeRef.getSimpleName(), typeRef.getSimpleName());
        return TypeReference.builder()
                .packageName(typeRef.getPackageName())
                .simpleName(newName)
                .primitive(typeRef.isPrimitive())
                .genericArguments(
                        Asserts.isNotEmpty(typeRef.getGenericArguments())
                                ? updateGenericArgumentsTypeReference(typeRef, remapping)
                                : null)
                .build();
    }

    private List<TypeReference> updateGenericArgumentsTypeReference(
            TypeReference typeRef, Map<String, String> remapping) {
        return typeRef.getGenericArguments().stream()
                .map(arg -> updateTypeReference(arg, remapping))
                .toList();
    }

    private boolean needsRewriting(TypeReference typeRef, Map<String, String> remapping) {
        if (remapping.containsKey(typeRef.getSimpleName())) {
            return true;
        }
        if (Asserts.isNotEmpty(typeRef.getGenericArguments())) {
            for (TypeReference arg : typeRef.getGenericArguments()) {
                if (needsRewriting(arg, remapping)) {
                    return true;
                }
            }
        }
        return false;
    }
}
