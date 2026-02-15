package com.voleksiienko.specforgeapi.core.application.service.ts;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.voleksiienko.specforgeapi.core.application.port.out.util.FingerprintGeneratorPort;
import com.voleksiienko.specforgeapi.core.application.port.out.util.StringInflectorPort;
import com.voleksiienko.specforgeapi.core.application.service.ts.inner.TsDeclarationDeduplicator;
import com.voleksiienko.specforgeapi.core.application.service.ts.inner.TsDeclarationFactory;
import com.voleksiienko.specforgeapi.core.application.service.ts.inner.TsFieldSorter;
import com.voleksiienko.specforgeapi.core.application.service.ts.inner.type.TsTypeReferenceCreator;
import com.voleksiienko.specforgeapi.core.application.service.ts.inner.type.TsTypeReferenceCreatorFacade;
import com.voleksiienko.specforgeapi.core.application.service.ts.inner.type.impl.*;
import com.voleksiienko.specforgeapi.core.domain.model.config.BaseConfig;
import com.voleksiienko.specforgeapi.core.domain.model.config.TypeScriptConfig;
import com.voleksiienko.specforgeapi.core.domain.model.config.TypeScriptConfig.Enums;
import com.voleksiienko.specforgeapi.core.domain.model.config.TypeScriptConfig.Enums.EnumStyle;
import com.voleksiienko.specforgeapi.core.domain.model.config.TypeScriptConfig.Structure;
import com.voleksiienko.specforgeapi.core.domain.model.config.TypeScriptConfig.Structure.DeclarationStyle;
import com.voleksiienko.specforgeapi.core.domain.model.spec.SpecModel;
import com.voleksiienko.specforgeapi.core.domain.model.spec.SpecProperty;
import com.voleksiienko.specforgeapi.core.domain.model.spec.type.*;
import com.voleksiienko.specforgeapi.core.domain.model.ts.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SpecModelToTsModelMapperIntegrationTest {

    private final FingerprintGeneratorPort fingerprintPort = mock(FingerprintGeneratorPort.class);
    private final StringInflectorPort inflector = mock(StringInflectorPort.class);
    private SpecModelToTsModelMapper mapper;

    @BeforeEach
    void setUp() {
        ArrayList<TsTypeReferenceCreator> creators = new ArrayList<>();
        TsTypeReferenceCreatorFacade facade = new TsTypeReferenceCreatorFacade(creators);
        TsFieldSorter tsFieldSorter = new TsFieldSorter();
        TsDeclarationFactory declarationFactory = new TsDeclarationFactory(facade, tsFieldSorter);
        creators.add(new TsPrimitiveTypeReferenceCreator());
        creators.add(new TsDateTypeReferenceCreator());
        creators.add(new TsEnumTypeReferenceCreator(inflector));
        creators.add(new TsListTypeReferenceCreator(facade));
        creators.add(new TsMapTypeReferenceCreator(facade));
        creators.add(new TsObjectTypeReferenceCreator(declarationFactory, inflector));
        TsDeclarationDeduplicator deduplicator = new TsDeclarationDeduplicator(fingerprintPort);
        mapper = new SpecModelToTsModelMapper(declarationFactory, deduplicator);

        when(inflector.capitalize("status")).thenReturn("Status");
        when(inflector.singularize("items")).thenReturn("item");
        when(inflector.capitalize("item")).thenReturn("Item");
        when(inflector.capitalize("role")).thenReturn("Role");
        when(fingerprintPort.map(any())).thenAnswer(inv -> {
            TsDeclaration decl = inv.getArgument(0);
            return decl.getClass().getSimpleName() + ":" + decl.getName();
        });
    }

    @Test
    void shouldHandleFullComplexTreeWithInterfaceAndTsEnum() {
        SpecModel specModel = buildComplexSpecModel();
        TypeScriptConfig config = buildConfig("OrderDto", DeclarationStyle.INTERFACE, EnumStyle.TS_ENUM);

        List<TsDeclaration> declarations = mapper.map(specModel, config);

        assertThat(declarations).hasSize(3);

        // Verify Root (Interface)
        TsInterface root = (TsInterface) findDeclaration(declarations, "OrderDto");
        assertThat(root).isNotNull();
        assertThat(root.getFields())
                .extracting(TsField::getName)
                .containsExactly("active", "id", "items", "meta", "status", "tags");

        // Verify List<String>
        TsField tags = getField(root, "tags");
        assertThat(tags.getType().getTypeName()).isEqualTo("Array");
        assertThat(tags.getType().getGenericArguments().getFirst().getTypeName())
                .isEqualTo("string");

        // Verify Map<String, Integer>
        TsField meta = getField(root, "meta");
        assertThat(meta.getType().getTypeName()).isEqualTo("Record");
        assertThat(meta.getType().getGenericArguments().get(0).getTypeName()).isEqualTo("string");
        assertThat(meta.getType().getGenericArguments().get(1).getTypeName()).isEqualTo("number");

        // Verify Enum (TS_ENUM style)
        TsField statusField = getField(root, "status");
        assertThat(statusField.getType().getTypeName()).isEqualTo("Status");

        TsEnum statusEnum = (TsEnum) findDeclaration(declarations, "Status");
        assertThat(statusEnum.getConstants())
                .extracting(TsEnumConstant::getKey)
                .containsExactlyInAnyOrder("OPEN", "CLOSED");

        // Verify Nested Object List (List<Address>)
        TsField itemsField = getField(root, "items");
        assertThat(itemsField.getType().getTypeName()).isEqualTo("Array");
        String nestedTypeName =
                itemsField.getType().getGenericArguments().getFirst().getTypeName();
        assertThat(nestedTypeName).isEqualTo("Item");

        TsInterface nestedObj = (TsInterface) findDeclaration(declarations, "Item");
        assertThat(nestedObj).isNotNull();
        assertFieldType(nestedObj, "street", "string");

        // Verify Optionality
        assertThat(getField(root, "active").isOptional()).isTrue();
        assertThat(getField(root, "id").isOptional()).isFalse();
    }

    @Test
    void shouldHandleTypeAliasAndUnionEnum() {
        TypeScriptConfig config = buildConfig("Simple", DeclarationStyle.TYPE_ALIAS, EnumStyle.UNION_STRING);
        SpecProperty enumProp = SpecProperty.builder()
                .name("role")
                .required(true)
                .type(EnumSpecType.builder().values(Set.of("ADMIN", "USER")).build())
                .build();
        SpecModel specModel = SpecModel.builder()
                .wrapperType(SpecModel.WrapperType.OBJECT)
                .properties(List.of(enumProp))
                .build();

        List<TsDeclaration> declarations = mapper.map(specModel, config);

        TsTypeAlias root = (TsTypeAlias) findDeclaration(declarations, "Simple");
        assertThat(root).isNotNull();
        TsUnionType roleEnum = (TsUnionType) findDeclaration(declarations, "Role");
        assertThat(roleEnum).isNotNull();
        assertThat(roleEnum.getValues()).containsExactlyInAnyOrder("ADMIN", "USER");
    }

    @Test
    void shouldHandleAllPrimitiveTypes() {
        SpecModel specModel = buildSpecModelWithAllPrimitives();
        TypeScriptConfig config = buildConfig("OrderDto", DeclarationStyle.INTERFACE, EnumStyle.TS_ENUM);

        List<TsDeclaration> declarations = mapper.map(specModel, config);

        TsInterface root = (TsInterface) declarations.getFirst();
        assertFieldType(root, "s", "string");
        assertFieldType(root, "i", "number");
        assertFieldType(root, "d", "number");
        assertFieldType(root, "dec", "number");
        assertFieldType(root, "b", "boolean");
        assertFieldType(root, "date", "string");
        assertFieldType(root, "time", "string");
        assertFieldType(root, "dt", "string");
    }

    private TsDeclaration findDeclaration(List<TsDeclaration> declarations, String name) {
        return declarations.stream()
                .filter(d -> d.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    private TsField getField(TsInterface tsInterface, String fieldName) {
        return tsInterface.getFields().stream()
                .filter(f -> f.getName().equals(fieldName))
                .findFirst()
                .orElseThrow();
    }

    private void assertFieldType(TsInterface tsInterface, String fieldName, String expectedTypeName) {
        assertThat(getField(tsInterface, fieldName).getType().getTypeName()).isEqualTo(expectedTypeName);
    }

    private TypeScriptConfig buildConfig(String name, DeclarationStyle structureStyle, EnumStyle enumStyle) {
        return new TypeScriptConfig(
                new BaseConfig(
                        new BaseConfig.Naming(name), new BaseConfig.Fields(BaseConfig.Fields.SortType.ALPHABETICAL)),
                new Structure(structureStyle),
                new Enums(enumStyle));
    }

    private SpecModel buildComplexSpecModel() {
        return SpecModel.builder()
                .wrapperType(SpecModel.WrapperType.OBJECT)
                .properties(List.of(
                        SpecProperty.builder()
                                .name("id")
                                .required(true)
                                .type(IntegerSpecType.builder().build())
                                .build(),
                        SpecProperty.builder()
                                .name("active")
                                .required(false)
                                .type(new BooleanSpecType())
                                .build(),
                        SpecProperty.builder()
                                .name("tags")
                                .required(true)
                                .type(ListSpecType.builder()
                                        .valueType(StringSpecType.builder().build())
                                        .build())
                                .build(),
                        SpecProperty.builder()
                                .name("meta")
                                .required(true)
                                .type(MapSpecType.builder()
                                        .keyType(StringSpecType.builder().build())
                                        .valueType(IntegerSpecType.builder().build())
                                        .build())
                                .build(),
                        SpecProperty.builder()
                                .name("status")
                                .required(true)
                                .type(EnumSpecType.builder()
                                        .values(Set.of("OPEN", "CLOSED"))
                                        .build())
                                .build(),
                        SpecProperty.builder()
                                .name("items")
                                .required(true)
                                .type(ListSpecType.builder()
                                        .valueType(ObjectSpecType.builder()
                                                .children(List.of(SpecProperty.builder()
                                                        .name("street")
                                                        .required(true)
                                                        .type(StringSpecType.builder()
                                                                .build())
                                                        .build()))
                                                .build())
                                        .build())
                                .build()))
                .build();
    }

    private SpecModel buildSpecModelWithAllPrimitives() {
        return SpecModel.builder()
                .wrapperType(SpecModel.WrapperType.OBJECT)
                .properties(List.of(
                        SpecProperty.builder()
                                .name("s")
                                .required(true)
                                .type(StringSpecType.builder().build())
                                .build(),
                        SpecProperty.builder()
                                .name("i")
                                .required(true)
                                .type(IntegerSpecType.builder().build())
                                .build(),
                        SpecProperty.builder()
                                .name("d")
                                .required(true)
                                .type(DoubleSpecType.builder().build())
                                .build(),
                        SpecProperty.builder()
                                .name("dec")
                                .required(true)
                                .type(DecimalSpecType.builder().build())
                                .build(),
                        SpecProperty.builder()
                                .name("b")
                                .required(true)
                                .type(new BooleanSpecType())
                                .build(),
                        SpecProperty.builder()
                                .name("date")
                                .required(true)
                                .type(DateSpecType.builder()
                                        .format("yyyy-MM-dd")
                                        .build())
                                .build(),
                        SpecProperty.builder()
                                .name("time")
                                .required(true)
                                .type(TimeSpecType.builder().format("HH:mm").build())
                                .build(),
                        SpecProperty.builder()
                                .name("dt")
                                .required(true)
                                .type(DateTimeSpecType.builder()
                                        .format("dd.MM HH:mm")
                                        .build())
                                .build()))
                .build();
    }
}
