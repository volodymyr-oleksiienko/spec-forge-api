package com.voleksiienko.specforgeapi.infra.adapter.out.java;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.voleksiienko.specforgeapi.core.application.exception.ConversionException;
import com.voleksiienko.specforgeapi.core.domain.model.java.*;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class JavaModelToJavaCodeAdapterTest {

    private final JavaModelToJavaCodeAdapter adapter = new JavaModelToJavaCodeAdapter();

    private JavaClass wrapInRoot(String rootName, JavaType nested) {
        JavaField dummy = createField("dummy", createPrimitive("int"), null);
        return JavaClass.createClass(rootName, null, List.of(dummy), List.of(nested));
    }

    private JavaField createField(String name, TypeReference type, List<JavaAnnotation> annotations) {
        return JavaField.builder()
                .name(name)
                .type(type)
                .annotations(annotations)
                .build();
    }

    private TypeReference createPrimitive(String name) {
        return TypeReference.builder().simpleName(name).primitive(true).build();
    }

    private TypeReference createType(String pkg, String name, boolean isPrimitive) {
        return TypeReference.builder()
                .packageName(pkg)
                .simpleName(name)
                .primitive(isPrimitive)
                .build();
    }

    private JavaAnnotation createAnnotation(String pkg, String name, Map<String, String> attrs) {
        return JavaAnnotation.builder()
                .packageName(pkg)
                .simpleName(name)
                .attributes(attrs)
                .build();
    }

    @Nested
    class RootTypes {

        @Test
        void shouldMapStandardClass() {
            JavaAnnotation entityAnn = createAnnotation("javax.persistence", "Entity", null);
            JavaField idField = createField("id", createType("java.lang", "Long", false), null);
            JavaClass root = JavaClass.createClass("User", List.of(entityAnn), List.of(idField));

            String code = adapter.map(root);

            assertThat(code)
                    .contains("@Entity")
                    .contains("public class User {")
                    .contains("private Long id;")
                    .doesNotContain("static class");
        }

        @Test
        void shouldMapRecord() {
            JavaField xField = createField("x", createPrimitive("int"), null);
            JavaField yField = createField("y", createPrimitive("int"), null);
            JavaClass pointRecord = JavaClass.createRecord("Point", null, List.of(xField, yField));

            String code = adapter.map(pointRecord);

            assertThat(code)
                    .contains("public record Point(int x, int y) {")
                    .doesNotContain("private int x;")
                    .doesNotContain("final int y;");
        }
    }

    @Nested
    class NestedTypes {

        @Test
        void shouldMapNestedClass() {
            JavaClass inner =
                    JavaClass.createClass("Inner", null, List.of(createField("val", createPrimitive("int"), null)));
            JavaClass root = wrapInRoot("Outer", inner);

            String code = adapter.map(root);

            assertThat(code)
                    .contains("public class Outer {")
                    .contains("  public static class Inner {"); // todo use Inner as field type
        }

        @Test
        void shouldMapSimpleEnum() {
            JavaEnum simpleEnum = JavaEnum.of(
                    "Status",
                    null,
                    null,
                    List.of(
                            JavaEnumConstant.builder().name("ACTIVE").build(),
                            JavaEnumConstant.builder().name("INACTIVE").build()));
            JavaClass root = wrapInRoot("Container", simpleEnum);

            String code = adapter.map(root);

            assertThat(code)
                    .contains("public enum Status {")
                    .contains("ACTIVE,")
                    .contains("INACTIVE")
                    .doesNotContain("private final");
        }

        @Test
        void shouldMapComplexEnum() {
            JavaField valueField = createField("code", createPrimitive("int"), null);
            JavaEnum complexEnum = JavaEnum.of(
                    "ErrorCode",
                    List.of(createAnnotation("lombok", "AllArgsConstructor", null)),
                    List.of(valueField),
                    List.of(
                            JavaEnumConstant.builder()
                                    .name("NOT_FOUND")
                                    .arguments(List.of("404"))
                                    .build(),
                            JavaEnumConstant.builder()
                                    .name("SERVER_ERROR")
                                    .arguments(List.of("500"))
                                    .build()));
            JavaClass root = wrapInRoot("AppErrors", complexEnum);

            String code = adapter.map(root);

            assertThat(code)
                    .contains("@AllArgsConstructor")
                    .contains("public enum ErrorCode {")
                    .contains("NOT_FOUND(404),")
                    .contains("SERVER_ERROR(500);")
                    .contains("private final int code;");
        }
    }

    @Nested
    class TypeResolution {

        @Test
        void shouldResolvePrimitives() {
            List<JavaField> fields = List.of(
                    createField("f1", createPrimitive("int"), null),
                    createField("f2", createPrimitive("long"), null),
                    createField("f3", createPrimitive("double"), null),
                    createField("f4", createPrimitive("float"), null),
                    createField("f5", createPrimitive("boolean"), null),
                    createField("f6", createPrimitive("char"), null),
                    createField("f7", createPrimitive("byte"), null),
                    createField("f8", createPrimitive("short"), null));
            JavaClass root = JavaClass.createClass("Primitives", null, fields);

            String code = adapter.map(root);

            assertThat(code)
                    .contains("int f1")
                    .contains("long f2")
                    .contains("double f3")
                    .contains("float f4")
                    .contains("boolean f5")
                    .contains("char f6")
                    .contains("byte f7")
                    .contains("short f8");
        }

        @Test
        void shouldThrowOnUnknownPrimitive() {
            JavaField invalidField = createField("bad", createPrimitive("uint128"), null);
            JavaClass root = JavaClass.createClass("Broken", null, List.of(invalidField));

            assertThatThrownBy(() -> adapter.map(root))
                    .isInstanceOf(ConversionException.class)
                    .hasMessageContaining("Unknown primitive type: uint128");
        }

        @Test
        void shouldResolveGenerics() {
            TypeReference stringType = createType("java.lang", "String", false);
            TypeReference listType = TypeReference.builder()
                    .packageName("java.util")
                    .simpleName("List")
                    .genericArguments(List.of(stringType))
                    .build();
            JavaClass root = JavaClass.createClass("Dto", null, List.of(createField("items", listType, null)));

            String code = adapter.map(root);

            assertThat(code).contains("private List<String> items;");
        }

        @Test
        void shouldResolveComplexGenerics() {
            TypeReference stringType = createType("java.lang", "String", false);
            TypeReference intType = createType("java.lang", "Integer", false);
            TypeReference listIntType = TypeReference.builder()
                    .packageName("java.util")
                    .simpleName("List")
                    .genericArguments(List.of(intType))
                    .build();
            TypeReference mapType = TypeReference.builder()
                    .packageName("java.util")
                    .simpleName("Map")
                    .genericArguments(List.of(stringType, listIntType))
                    .build();
            JavaClass root = JavaClass.createClass("Complex", null, List.of(createField("data", mapType, null)));

            String code = adapter.map(root);

            assertThat(code).contains("private Map<String, List<Integer>> data;");
        }
    }

    @Nested
    class Annotations {

        @Test
        void shouldMapFieldAnnotations() {
            JavaAnnotation columnAnn =
                    createAnnotation("javax.persistence", "Column", Map.of("name", "\"user_id\"", "nullable", "false"));
            JavaClass root = JavaClass.createClass(
                    "Entity", null, List.of(createField("id", createPrimitive("long"), List.of(columnAnn))));

            String code = adapter.map(root);

            assertThat(code).contains("@Column(").contains("name = \"user_id\"").contains("nullable = false");
        }

        @Test
        void shouldMapRecordComponentAnnotations() {
            JavaAnnotation notNull = createAnnotation("jakarta.validation", "NotNull", null);
            JavaField field = createField("email", createType("java.lang", "String", false), List.of(notNull));
            JavaClass record = JavaClass.createRecord("User", null, List.of(field));

            String code = adapter.map(record);

            assertThat(code).contains("(@NotNull String email)");
        }
    }
}
