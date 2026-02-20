package com.voleksiienko.specforgeapi.infra.adapter.out.util;

import static org.assertj.core.api.Assertions.assertThat;

import com.voleksiienko.specforgeapi.core.domain.model.java.JavaClass;
import com.voleksiienko.specforgeapi.core.domain.model.java.JavaField;
import com.voleksiienko.specforgeapi.core.domain.model.java.TypeReference;
import java.util.List;
import org.junit.jupiter.api.Test;

class FingerprintGeneratorAdapterTest {

    private final FingerprintGeneratorAdapter adapter = new FingerprintGeneratorAdapter();

    @Test
    void shouldIgnoreNameInFingerprint() {
        var classA = createSimpleClass("UserDto");
        var classB = createSimpleClass("AccountDto");

        String fingerprintA = adapter.map(classA);
        String fingerprintB = adapter.map(classB);

        assertThat(fingerprintA)
                .isEqualTo(fingerprintB)
                .doesNotContain("UserDto")
                .doesNotContain("AccountDto");
    }

    @Test
    void shouldDetectStructuralDifferences() {
        var classA = createSimpleClass("UserDto");
        var classB = JavaClass.createClass(
                "UserDto",
                null,
                List.of(JavaField.builder()
                        .name("email")
                        .type(TypeReference.builder()
                                .simpleName("String")
                                .packageName("java.lang")
                                .build())
                        .build()));

        String fingerprintA = adapter.map(classA);
        String fingerprintB = adapter.map(classB);

        assertThat(fingerprintA).isNotEqualTo(fingerprintB);
    }

    @Test
    void shouldDistinguishRecordsFromClasses() {
        var standardClass = createSimpleClass("UserDto");
        var recordClass = JavaClass.createRecord("UserDto", null, standardClass.getFields());

        String fpClass = adapter.map(standardClass);
        String fpRecord = adapter.map(recordClass);

        assertThat(fpClass).isNotEqualTo(fpRecord);
    }

    private JavaClass createSimpleClass(String className) {
        return JavaClass.createClass(
                className,
                null,
                List.of(JavaField.builder()
                        .name("id")
                        .type(TypeReference.builder()
                                .simpleName("String")
                                .packageName("java.lang")
                                .build())
                        .build()));
    }
}
