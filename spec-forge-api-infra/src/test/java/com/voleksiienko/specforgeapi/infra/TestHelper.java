package com.voleksiienko.specforgeapi.infra;

public class TestHelper {

    private TestHelper() {}

    public static String readResource(String path) {
        try (var inputStream = TestHelper.class.getResourceAsStream(path)) {
            if (inputStream == null) {
                throw new IllegalArgumentException("Resource not found: %s".formatted(path));
            }
            return new String(inputStream.readAllBytes());
        } catch (Exception e) {
            throw new RuntimeException("Failed to read resource: %s".formatted(path), e);
        }
    }
}
