package com.voleksiienko.specforgeapi.core.application.service.ts.inner;

import com.voleksiienko.specforgeapi.core.application.annotation.Component;
import java.util.Set;

@Component
public class TypeScriptFieldNameSanitizer {

    private static final Set<String> KEYWORDS = Set.of(
            "break",
            "case",
            "catch",
            "class",
            "const",
            "continue",
            "debugger",
            "default",
            "delete",
            "do",
            "else",
            "enum",
            "export",
            "extends",
            "false",
            "finally",
            "for",
            "function",
            "if",
            "import",
            "in",
            "instanceof",
            "new",
            "null",
            "return",
            "super",
            "switch",
            "this",
            "throw",
            "true",
            "try",
            "typeof",
            "var",
            "void",
            "while",
            "with",
            "yield",
            "await",
            "implements",
            "interface",
            "let",
            "package",
            "private",
            "protected",
            "public",
            "static",
            "any",
            "boolean",
            "constructor",
            "declare",
            "get",
            "module",
            "require",
            "number",
            "set",
            "string",
            "symbol",
            "type",
            "from",
            "of",
            "as",
            "unknown",
            "never",
            "readonly",
            "keyof",
            "namespace",
            "infer",
            "is");

    public String sanitize(String input) {
        StringBuilder sb = new StringBuilder();

        char c = input.charAt(0);

        if (isTypeScriptIdentifierStart(c)) {
            sb.append(c);
        } else {
            sb.append('_');
            if (isTypeScriptIdentifierPart(c)) {
                sb.append(c);
            }
        }

        for (int i = 1; i < input.length(); i++) {
            c = input.charAt(i);
            sb.append(isTypeScriptIdentifierPart(c) ? c : '_');
        }

        String result = sb.toString();

        return KEYWORDS.contains(result) ? "%sField".formatted(result) : result;
    }

    private boolean isTypeScriptIdentifierStart(char c) {
        return Character.isLetter(c) || c == '$' || c == '_';
    }

    private boolean isTypeScriptIdentifierPart(char c) {
        return Character.isLetterOrDigit(c) || c == '$' || c == '_';
    }
}
