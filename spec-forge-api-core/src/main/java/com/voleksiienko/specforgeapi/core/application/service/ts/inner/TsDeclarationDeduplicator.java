package com.voleksiienko.specforgeapi.core.application.service.ts.inner;

import com.voleksiienko.specforgeapi.core.application.annotation.Component;
import com.voleksiienko.specforgeapi.core.application.port.out.util.FingerprintGeneratorPort;
import com.voleksiienko.specforgeapi.core.common.Asserts;
import com.voleksiienko.specforgeapi.core.domain.model.ts.*;
import java.util.*;

@Component
public class TsDeclarationDeduplicator {

    private final FingerprintGeneratorPort fingerprintGenerator;

    public TsDeclarationDeduplicator(FingerprintGeneratorPort fingerprintGenerator) {
        this.fingerprintGenerator = fingerprintGenerator;
    }

    /**
     * Identifies duplicate TsDeclaration-s (same structure but different names) and removes them
     * Rewrites references in the remaining interfaces/aliases to point to the canonical versions
     */
    public List<TsDeclaration> deduplicate(List<TsDeclaration> candidates) {
        // Map: Structural Signature -> Canonical TsDeclaration
        Map<String, TsDeclaration> uniqueTypes = new LinkedHashMap<>();
        // Map: Duplicate Name -> Canonical Name
        Map<String, String> remapping = new HashMap<>();

        for (TsDeclaration candidate : candidates) {
            String signature = fingerprintGenerator.map(candidate);

            if (uniqueTypes.containsKey(signature)) {
                // Duplicate found
                TsDeclaration canonical = uniqueTypes.get(signature);
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
                .map(declaration -> replaceReferences(declaration, remapping))
                .toList();
    }

    private TsDeclaration replaceReferences(TsDeclaration declaration, Map<String, String> remapping) {
        return switch (declaration) {
            case TsInterface tsInterface -> {
                if (noFieldsNeedRewriting(tsInterface.getFields(), remapping)) {
                    yield tsInterface;
                }
                yield TsInterface.builder()
                        .name(tsInterface.getName())
                        .fields(updateFields(tsInterface.getFields(), remapping))
                        .build();
            }
            case TsTypeAlias tsTypeAlias -> {
                if (noFieldsNeedRewriting(tsTypeAlias.getFields(), remapping)) {
                    yield tsTypeAlias;
                }
                yield TsTypeAlias.builder()
                        .name(tsTypeAlias.getName())
                        .fields(updateFields(tsTypeAlias.getFields(), remapping))
                        .build();
            }
            case TsEnum tsEnum -> tsEnum;
            case TsUnionType tsUnion -> tsUnion;
        };
    }

    private boolean noFieldsNeedRewriting(List<TsField> fields, Map<String, String> remapping) {
        return fields.stream().noneMatch(f -> needsRewriting(f.getType(), remapping));
    }

    private boolean needsRewriting(TsTypeReference typeRef, Map<String, String> remapping) {
        if (remapping.containsKey(typeRef.getTypeName())) {
            return true;
        }
        if (Asserts.isNotEmpty(typeRef.getGenericArguments())) {
            for (TsTypeReference arg : typeRef.getGenericArguments()) {
                if (needsRewriting(arg, remapping)) {
                    return true;
                }
            }
        }
        return false;
    }

    private List<TsField> updateFields(List<TsField> fields, Map<String, String> remapping) {
        return fields.stream().map(field -> updateField(field, remapping)).toList();
    }

    private TsField updateField(TsField field, Map<String, String> remapping) {
        TsTypeReference updatedType = updateTypeReference(field.getType(), remapping);
        return TsField.builder()
                .name(field.getName())
                .type(updatedType)
                .optional(field.isOptional())
                .build();
    }

    private TsTypeReference updateTypeReference(TsTypeReference typeRef, Map<String, String> remapping) {
        String newName = remapping.getOrDefault(typeRef.getTypeName(), typeRef.getTypeName());
        return TsTypeReference.builder()
                .typeName(newName)
                .genericArguments(
                        Asserts.isNotEmpty(typeRef.getGenericArguments())
                                ? updateGenericArguments(typeRef, remapping)
                                : null)
                .build();
    }

    private List<TsTypeReference> updateGenericArguments(TsTypeReference typeRef, Map<String, String> remapping) {
        return typeRef.getGenericArguments().stream()
                .map(arg -> updateTypeReference(arg, remapping))
                .toList();
    }
}
