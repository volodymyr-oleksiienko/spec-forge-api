package com.voleksiienko.specforgeapi.core.application.service.java.inner;

import com.voleksiienko.specforgeapi.core.application.annotation.Component;
import java.util.Set;

@Component
public class JavaFieldNameSanitizer {

    private static final Set<String> KEYWORDS = Set.of(
            "abstract",
            "assert",
            "boolean",
            "break",
            "byte",
            "case",
            "catch",
            "char",
            "class",
            "const",
            "continue",
            "default",
            "do",
            "double",
            "else",
            "enum",
            "extends",
            "final",
            "finally",
            "float",
            "for",
            "goto",
            "if",
            "implements",
            "import",
            "instanceof",
            "int",
            "interface",
            "long",
            "native",
            "new",
            "package",
            "private",
            "protected",
            "public",
            "return",
            "short",
            "static",
            "strictfp",
            "super",
            "switch",
            "synchronized",
            "this",
            "throw",
            "throws",
            "transient",
            "try",
            "void",
            "volatile",
            "while",
            "true",
            "false",
            "null",
            "var",
            "yield",
            "record",
            "_");

    public String sanitize(String input) {
        StringBuilder sb = new StringBuilder();

        char c = input.charAt(0);

        if (Character.isJavaIdentifierStart(c)) {
            sb.append(c);
        } else {
            sb.append('_');
            if (Character.isJavaIdentifierPart(c)) {
                sb.append(c);
            }
        }

        for (int i = 1; i < input.length(); i++) {
            c = input.charAt(i);
            sb.append(Character.isJavaIdentifierPart(c) ? c : '_');
        }

        String result = sb.toString();

        return KEYWORDS.contains(result) ? "%sField".formatted(result) : result;
    }
}
