package com.voleksiienko.specforgeapi.infra.adapter.out.ts.inner;

import com.voleksiienko.specforgeapi.core.common.Asserts;
import com.voleksiienko.specforgeapi.core.domain.model.ts.*;
import java.util.List;
import java.util.stream.Collectors;

public class TsCodeWriter {

    private static final String INDENT = "  ";
    private final StringBuilder buffer = new StringBuilder();
    private int indentLevel = 0;

    public void writeInterface(TsInterface tsInterface) {
        line("export interface %s {".formatted(tsInterface.getName()));
        indentLevel++;
        writeFields(tsInterface.getFields());
        indentLevel--;
        line("}");
        emptyLine();
    }

    public void writeTypeAlias(TsTypeAlias tsTypeAlias) {
        line("export type %s = {".formatted(tsTypeAlias.getName()));
        indentLevel++;
        writeFields(tsTypeAlias.getFields());
        indentLevel--;
        line("};");
        emptyLine();
    }

    public void writeEnum(TsEnum tsEnum) {
        line("export enum %s {".formatted(tsEnum.getName()));
        indentLevel++;
        List<TsEnumConstant> constants = tsEnum.getConstants();
        for (int i = 0; i < constants.size(); i++) {
            TsEnumConstant c = constants.get(i);
            String suffix = (i < constants.size() - 1) ? "," : "";
            line("%s = '%s'%s".formatted(c.getKey(), c.getValue(), suffix));
        }
        indentLevel--;
        line("}");
        emptyLine();
    }

    public void writeUnion(TsUnionType tsUnion) {
        String unionString =
                tsUnion.getValues().stream().map(v -> "'" + v + "'").collect(Collectors.joining(" | "));
        line("export type %s = %s;".formatted(tsUnion.getName(), unionString));
        emptyLine();
    }

    private void writeFields(List<TsField> fields) {
        fields.forEach(field -> {
            String suffix = field.isOptional() ? "?" : "";
            String typeName = resolveTypeName(field.getType());
            line("%s%s: %s;".formatted(field.getName(), suffix, typeName));
        });
    }

    private String resolveTypeName(TsTypeReference typeRef) {
        if ("Array".equals(typeRef.getTypeName())
                && Asserts.isNotEmpty(typeRef.getGenericArguments())
                && typeRef.getGenericArguments().size() == 1) {
            TsTypeReference innerType = typeRef.getGenericArguments().getFirst();
            if (Asserts.isEmpty(innerType.getGenericArguments())) {
                return resolveTypeName(innerType) + "[]";
            }
        }
        if (Asserts.isEmpty(typeRef.getGenericArguments())) {
            return typeRef.getTypeName();
        }
        String args = typeRef.getGenericArguments().stream()
                .map(this::resolveTypeName)
                .collect(Collectors.joining(", "));
        return "%s<%s>".formatted(typeRef.getTypeName(), args);
    }

    @Override
    public String toString() {
        return buffer.toString();
    }

    private void line(String text) {
        buffer.append(INDENT.repeat(Math.max(0, indentLevel))).append(text).append("\n");
    }

    private void emptyLine() {
        buffer.append("\n");
    }
}
