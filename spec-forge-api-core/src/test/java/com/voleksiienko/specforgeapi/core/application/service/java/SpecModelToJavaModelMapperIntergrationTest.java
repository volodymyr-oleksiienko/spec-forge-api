package com.voleksiienko.specforgeapi.core.application.service.java;

import static com.voleksiienko.specforgeapi.core.domain.model.config.BaseConfig.Fields.SortType.AS_IS;
import static com.voleksiienko.specforgeapi.core.domain.model.config.JavaConfig.Serialization.JsonPropertyMode.ALWAYS;
import static com.voleksiienko.specforgeapi.core.domain.model.config.JavaConfig.Structure.Type.CLASS;
import static com.voleksiienko.specforgeapi.core.domain.model.config.JavaConfig.Structure.Type.RECORD;
import static com.voleksiienko.specforgeapi.core.domain.model.spec.type.StringSpecType.StringTypeFormat.UUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.voleksiienko.specforgeapi.core.application.port.out.util.FingerprintGeneratorPort;
import com.voleksiienko.specforgeapi.core.application.port.out.util.StringInflectorPort;
import com.voleksiienko.specforgeapi.core.application.service.java.inner.*;
import com.voleksiienko.specforgeapi.core.application.service.java.inner.type.JavaClassNameCreator;
import com.voleksiienko.specforgeapi.core.application.service.java.inner.type.JavaTypeReferenceCreator;
import com.voleksiienko.specforgeapi.core.application.service.java.inner.type.JavaTypeReferenceCreatorFacade;
import com.voleksiienko.specforgeapi.core.application.service.java.inner.type.impl.*;
import com.voleksiienko.specforgeapi.core.domain.model.config.BaseConfig;
import com.voleksiienko.specforgeapi.core.domain.model.config.JavaConfig;
import com.voleksiienko.specforgeapi.core.domain.model.java.JavaClass;
import com.voleksiienko.specforgeapi.core.domain.model.java.JavaEnum;
import com.voleksiienko.specforgeapi.core.domain.model.java.JavaField;
import com.voleksiienko.specforgeapi.core.domain.model.java.TypeReference;
import com.voleksiienko.specforgeapi.core.domain.model.spec.SpecModel;
import com.voleksiienko.specforgeapi.core.domain.model.spec.SpecProperty;
import com.voleksiienko.specforgeapi.core.domain.model.spec.type.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SpecModelToJavaModelMapperIntergrationTest {

    private final FingerprintGeneratorPort fingerprintPort = mock(FingerprintGeneratorPort.class);
    private final StringInflectorPort inflector = mock(StringInflectorPort.class);
    private SpecModelToJavaModelMapper mapper;

    @BeforeEach
    void setUp() {
        setupInflector();
        setupFingerprinter();
        var annotationsSupplier = new JavaAnnotationsSupplier();
        var javaFieldSorter = new JavaFieldSorter();
        var classNameCreator = new JavaClassNameCreator(inflector);
        var javaEnumFactory = new JavaEnumFactory(annotationsSupplier);
        List<JavaTypeReferenceCreator> creators = new ArrayList<>();
        var realFacade = new JavaTypeReferenceCreatorFacade(creators);
        var javaClassFactory = new JavaClassFactory(realFacade, javaFieldSorter, annotationsSupplier);
        addTypeReferenceCreators(creators, javaEnumFactory, classNameCreator, realFacade, javaClassFactory);
        var deduplicator = new JavaClassDeduplicator(fingerprintPort);
        this.mapper = new SpecModelToJavaModelMapper(javaClassFactory, deduplicator);
    }

    @Test
    void shouldMapAndDeduplicate() {
        var addressSpec = ObjectSpecType.builder()
                .children(List.of(
                        createProp("city", StringSpecType.builder().build()),
                        createProp("zip", IntegerSpecType.builder().build())))
                .build();
        var userSpec = SpecModel.builder()
                .wrapperType(SpecModel.WrapperType.OBJECT)
                .properties(
                        List.of(createProp("shippingAddress", addressSpec), createProp("billingAddress", addressSpec)))
                .build();
        JavaConfig config = createConfig(CLASS, true);

        JavaClass result = mapper.map(userSpec, config);

        assertThat(result.getName()).isEqualTo("RootDto");
        assertThat(result.getFields()).hasSize(2);
        assertThat(result.getNestedClasses()).hasSize(1).first().satisfies(nested -> assertThat(nested.getName())
                .isEqualTo("ShippingAddress"));
        assertThat(result.getFields().get(0).getType().getSimpleName()).isEqualTo("ShippingAddress");
        assertThat(result.getFields().get(1).getType().getSimpleName()).isEqualTo("BillingAddress");
    }

    @Test
    void shouldMapCollections() {
        var listType = ListSpecType.builder()
                .valueType(IntegerSpecType.builder().build())
                .build();
        var mapType = MapSpecType.builder()
                .keyType(StringSpecType.builder().build())
                .valueType(listType)
                .build();
        var spec = SpecModel.builder()
                .wrapperType(SpecModel.WrapperType.OBJECT)
                .properties(List.of(createProp("data", mapType)))
                .build();

        JavaClass result = mapper.map(spec, createConfig(CLASS, false));

        TypeReference type = result.getFields().getFirst().getType();
        assertThat(type.getPackageName()).isEqualTo("java.util");
        assertThat(type.getSimpleName()).isEqualTo("Map");
        assertThat(type.getGenericArguments().get(0).getSimpleName()).isEqualTo("String");
        TypeReference valueType = type.getGenericArguments().get(1);
        assertThat(valueType.getSimpleName()).isEqualTo("List");
        assertThat(valueType.getGenericArguments().getFirst().getSimpleName()).isIn("Integer", "Long");
    }

    @Test
    void shouldUseWrappersWhenValidated() {
        var spec = SpecModel.builder()
                .wrapperType(SpecModel.WrapperType.OBJECT)
                .properties(
                        List.of(createProp("count", IntegerSpecType.builder().build())))
                .build();

        JavaClass result = mapper.map(spec, createConfig(CLASS, true));

        assertThat(result.getFields().getFirst().getType().getSimpleName()).isEqualTo("Long");
        assertThat(result.getFields().getFirst().getType().isPrimitive()).isFalse();
    }

    @Test
    void shouldUsePrimitivesWhenNotValidated() {
        var spec = SpecModel.builder()
                .wrapperType(SpecModel.WrapperType.OBJECT)
                .properties(List.of(SpecProperty.builder()
                        .name("count")
                        .required(true)
                        .type(IntegerSpecType.builder().build())
                        .build()))
                .build();

        JavaClass result = mapper.map(spec, createConfig(CLASS, false));

        assertThat(result.getFields().getFirst().getType().getSimpleName()).isEqualTo("long");
        assertThat(result.getFields().getFirst().getType().isPrimitive()).isTrue();
    }

    @Test
    void shouldMapStringEnums() {
        var enumType = EnumSpecType.builder().values(Set.of("A", "B")).build();
        var spec = SpecModel.builder()
                .wrapperType(SpecModel.WrapperType.OBJECT)
                .properties(List.of(createProp("status", enumType)))
                .build();

        JavaClass result = mapper.map(spec, createConfig(CLASS, true));

        assertThat(result.getNestedClasses()).hasSize(1);
        JavaEnum javaEnum = (JavaEnum) result.getNestedClasses().getFirst();
        assertThat(javaEnum.getName()).isEqualTo("Status");
        assertThat(javaEnum.getConstants()).extracting("name").containsExactlyInAnyOrder("A", "B");
    }

    @Test
    void shouldGenerateRecord() {
        var spec = SpecModel.builder()
                .wrapperType(SpecModel.WrapperType.OBJECT)
                .properties(List.of(createProp("id", StringSpecType.builder().build())))
                .build();

        JavaClass result = mapper.map(spec, createConfig(RECORD, true));

        assertThat(result.isRecord()).isTrue();
    }

    @Test
    void shouldMapTemporalTypes() {
        var spec = SpecModel.builder()
                .wrapperType(SpecModel.WrapperType.OBJECT)
                .properties(List.of(
                        createProp(
                                "date",
                                DateSpecType.builder().format("yyyy.MM.dd").build()),
                        createProp(
                                "time",
                                TimeSpecType.builder().format("HH:mm:ss").build()),
                        createProp(
                                "offsetTime",
                                TimeSpecType.builder().format("HH:mm:ssXXX").build()),
                        createProp(
                                "local",
                                DateTimeSpecType.builder()
                                        .format("yyyy-MM-dd'T'HH:mm:ss")
                                        .build()),
                        createProp(
                                "offset",
                                DateTimeSpecType.builder()
                                        .format("yyyy-MM-dd'T'HH:mm:ssXXX")
                                        .build())))
                .build();

        JavaClass result = mapper.map(spec, createConfig(CLASS, true));

        assertThat(result.getFields().get(0).getType().getSimpleName()).isEqualTo("LocalDate");
        assertThat(result.getFields().get(0).getType().getPackageName()).isEqualTo("java.time");
        assertThat(result.getFields().get(1).getType().getSimpleName()).isEqualTo("LocalTime");
        assertThat(result.getFields().get(1).getType().getPackageName()).isEqualTo("java.time");
        assertThat(result.getFields().get(2).getType().getSimpleName()).isEqualTo("OffsetTime");
        assertThat(result.getFields().get(2).getType().getPackageName()).isEqualTo("java.time");
        assertThat(result.getFields().get(3).getType().getSimpleName()).isEqualTo("LocalDateTime");
        assertThat(result.getFields().get(3).getType().getPackageName()).isEqualTo("java.time");
        assertThat(result.getFields().get(4).getType().getSimpleName()).isEqualTo("OffsetDateTime");
        assertThat(result.getFields().get(4).getType().getPackageName()).isEqualTo("java.time");
    }

    @Test
    void shouldMapBasicTypesAndFormats() {
        var spec = SpecModel.builder()
                .wrapperType(SpecModel.WrapperType.OBJECT)
                .properties(List.of(
                        createProp("active", new BooleanSpecType(), true),
                        createProp("flag", new BooleanSpecType(), false),
                        createProp("score", DoubleSpecType.builder().build(), true),
                        createProp("ratio", DoubleSpecType.builder().build(), false),
                        createProp("cost", DecimalSpecType.builder().build(), true),
                        createProp("name", StringSpecType.builder().build(), true),
                        createProp("uid", StringSpecType.builder().format(UUID).build(), true)))
                .build();

        JavaClass result = mapper.map(spec, createConfig(CLASS, false));

        List<JavaField> fields = result.getFields();
        assertThat(fields)
                .extracting("type.simpleName", "type.packageName", "type.primitive")
                .containsExactly(
                        tuple("boolean", null, true),
                        tuple("Boolean", "java.lang", false),
                        tuple("double", null, true),
                        tuple("Double", "java.lang", false),
                        tuple("BigDecimal", "java.math", false),
                        tuple("String", "java.lang", false),
                        tuple("UUID", "java.util", false));
    }

    private void setupInflector() {
        when(inflector.capitalize("shippingAddress")).thenReturn("ShippingAddress");
        when(inflector.capitalize("billingAddress")).thenReturn("BillingAddress");
        when(inflector.capitalize("status")).thenReturn("Status");
        when(inflector.capitalize("data")).thenReturn("Data");
        when(inflector.singularize("data")).thenReturn("data");
    }

    private void setupFingerprinter() {
        when(fingerprintPort.map(argThat(type -> type instanceof JavaClass javaClass
                        && javaClass.getFields().stream().anyMatch(f -> "city".equals(f.getName())))))
                .thenReturn("ADDRESS_STRUCT_HASH");
        when(fingerprintPort.map(argThat(type -> type instanceof JavaClass javaClass
                        && javaClass.getFields().stream().noneMatch(f -> "city".equals(f.getName())))))
                .thenReturn("OTHER_STRUCT_HASH");
    }

    private void addTypeReferenceCreators(
            List<JavaTypeReferenceCreator> strategies,
            JavaEnumFactory javaEnumFactory,
            JavaClassNameCreator classNameCreator,
            JavaTypeReferenceCreatorFacade realFacade,
            JavaClassFactory javaClassFactory) {
        strategies.add(new JavaStringTypeReferenceCreator());
        strategies.add(new JavaBooleanTypeReferenceCreator());
        strategies.add(new JavaIntegerTypeReferenceCreator());
        strategies.add(new JavaDoubleTypeReferenceCreator());
        strategies.add(new JavaDecimalTypeReferenceCreator());
        strategies.add(new JavaDateTypeReferenceCreator());
        strategies.add(new JavaTimeTypeReferenceCreator());
        strategies.add(new JavaDateTimeTypeReferenceCreator());
        strategies.add(new JavaEnumTypeReferenceCreator(javaEnumFactory, classNameCreator));
        strategies.add(new JavaListTypeReferenceCreator(realFacade));
        strategies.add(new JavaMapTypeReferenceCreator(realFacade));
        strategies.add(new JavaObjectTypeReferenceCreator(javaClassFactory, classNameCreator));
    }

    private SpecProperty createProp(String name, SpecType type) {
        return SpecProperty.builder().name(name).type(type).required(false).build();
    }

    private SpecProperty createProp(String name, SpecType type, boolean required) {
        return SpecProperty.builder().name(name).type(type).required(required).build();
    }

    private JavaConfig createConfig(JavaConfig.Structure.Type structureType, boolean validationEnabled) {
        return new JavaConfig(
                new BaseConfig(new BaseConfig.Naming("RootDto"), new BaseConfig.Fields(AS_IS)),
                new JavaConfig.Structure(structureType),
                new JavaConfig.Validation(validationEnabled),
                new JavaConfig.Builder(true, false),
                new JavaConfig.Serialization(ALWAYS));
    }
}
