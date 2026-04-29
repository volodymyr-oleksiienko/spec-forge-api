package com.voleksiienko.specforgeapi.core.application.service.java.inner;

import com.voleksiienko.specforgeapi.core.application.annotation.Component;
import com.voleksiienko.specforgeapi.core.common.Asserts;
import java.util.Set;
import java.util.regex.Pattern;

@Component
public class JavaFieldNameSanitizer {

    private static final Pattern WORDS_REGEX_PATTERN = Pattern.compile("[^a-zA-Z0-9]+|(?<=[a-z])(?=[A-Z])");
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

    public String toCamelCase(String input) {
        String[] words = WORDS_REGEX_PATTERN.split(input);

        StringBuilder result = new StringBuilder();

        for (String word : words) {
            if (Asserts.isBlank(word)) {
                continue;
            }
            if (result.isEmpty()) {
                result.append(word.toLowerCase());
            } else {
                result.append(Character.toUpperCase(word.charAt(0)));
                if (word.length() > 1) {
                    result.append(word.substring(1).toLowerCase());
                }
            }
        }

        return result.toString();
    }
}
