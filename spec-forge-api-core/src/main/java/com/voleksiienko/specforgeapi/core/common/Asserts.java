package com.voleksiienko.specforgeapi.core.common;

import java.util.Collection;
import java.util.Objects;

public final class Asserts {

    private Asserts() {}

    public static boolean isNotEmpty(Collection<?> collection) {
        return !isEmpty(collection);
    }

    public static boolean isEmpty(Collection<?> collection) {
        return Objects.isNull(collection) || collection.isEmpty();
    }

    public static boolean isNotBlank(String string) {
        return !isBlank(string);
    }

    public static boolean isBlank(String string) {
        return Objects.isNull(string) || string.isBlank();
    }
}
