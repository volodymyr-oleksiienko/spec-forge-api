package com.voleksiienko.specforgeapi.core.common;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;

public final class Asserts {

    private Asserts() {}

    public static boolean isNotEmpty(Collection<?> collection) {
        return !isEmpty(collection);
    }

    public static boolean isEmpty(Collection<?> collection) {
        return Objects.isNull(collection) || collection.isEmpty();
    }

    public static boolean isNotEmpty(Map<?, ?> map) {
        return !isEmpty(map);
    }

    public static boolean isEmpty(Map<?, ?> map) {
        return Objects.isNull(map) || map.isEmpty();
    }

    public static boolean isNotBlank(String string) {
        return !isBlank(string);
    }

    public static boolean isBlank(String string) {
        return Objects.isNull(string) || string.isBlank();
    }

    public static void requireNotBlank(String string, String message) {
        if (isBlank(string)) {
            throw new IllegalArgumentException(message);
        }
    }
}
