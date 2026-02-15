package com.voleksiienko.specforgeapi.infra.adapter.out.ts;

import static org.assertj.core.api.Assertions.assertThat;

import com.voleksiienko.specforgeapi.core.domain.model.ts.*;
import java.util.List;
import org.junit.jupiter.api.Test;

class TsModelToTsCodeAdapterTest {

    private final TsModelToTsCodeAdapter mapper = new TsModelToTsCodeAdapter();

    @Test
    void shouldWriteSimpleInterface() {
        var nameField = TsField.builder()
                .name("username")
                .type(TsTypeReference.builder().typeName("string").build())
                .build();
        var ageField = TsField.builder()
                .name("age")
                .type(TsTypeReference.builder().typeName("number").build())
                .build();
        var tsInterface = TsInterface.builder()
                .name("User")
                .fields(List.of(nameField, ageField))
                .build();

        String result = mapper.map(List.of(tsInterface));

        String expected = """
            export interface User {
              username: string;
              age: number;
            }

            """;
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void shouldWriteTypeAlias() {
        var field = TsField.builder()
                .name("id")
                .type(TsTypeReference.builder().typeName("string").build())
                .build();
        var typeAlias =
                TsTypeAlias.builder().name("UserId").fields(List.of(field)).build();

        String result = mapper.map(List.of(typeAlias));

        String expected = """
            export type UserId = {
              id: string;
            };

            """;
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void shouldWriteEnum() {
        var constants = List.of(
                TsEnumConstant.builder().key("RED").value("red").build(),
                TsEnumConstant.builder().key("BLUE").value("blue").build());
        var tsEnum = TsEnum.builder().name("Colors").constants(constants).build();

        String result = mapper.map(List.of(tsEnum));

        String expected = """
            export enum Colors {
              RED = 'red',
              BLUE = 'blue'
            }

            """;
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void shouldWriteUnionType() {
        var union = TsUnionType.builder()
                .name("Status")
                .values(List.of("OPEN", "CLOSED"))
                .build();

        String result = mapper.map(List.of(union));

        String expected = """
            export type Status = 'OPEN' | 'CLOSED';

            """;
        assertThat(result.toString()).isEqualTo(expected);
    }

    @Test
    void shouldAppendMultipleDeclarationsSequentially() {
        var union = TsUnionType.builder()
                .name("Role")
                .values(List.of("ADMIN", "USER"))
                .build();
        var field = TsField.builder()
                .name("role")
                .type(TsTypeReference.builder().typeName("Role").build())
                .build();
        var tsInterface =
                TsInterface.builder().name("User").fields(List.of(field)).build();

        String result = mapper.map(List.of(union, tsInterface));

        String expected = """
            export type Role = 'ADMIN' | 'USER';

            export interface User {
              role: Role;
            }

            """;
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void shouldWriteInterfaceWithOptionalAndGenericFields() {
        var stringType = TsTypeReference.builder().typeName("string").build();
        var listType = TsTypeReference.builder()
                .typeName("Array")
                .genericArguments(List.of(stringType))
                .build();
        var tagsField =
                TsField.builder().name("tags").type(listType).optional(true).build();
        var tsInterface =
                TsInterface.builder().name("Post").fields(List.of(tagsField)).build();

        String result = mapper.map(List.of(tsInterface));

        String expected = """
            export interface Post {
              tags?: string[];
            }

            """;
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void shouldHandleNestedGenerics() {
        var stringType = TsTypeReference.builder().typeName("string").build();
        var numberType = TsTypeReference.builder().typeName("number").build();
        var arrayType = TsTypeReference.builder()
                .typeName("Array")
                .genericArguments(List.of(numberType))
                .build();
        var recordType = TsTypeReference.builder()
                .typeName("Record")
                .genericArguments(List.of(stringType, arrayType))
                .build();
        var field = TsField.builder().name("data").type(recordType).build();
        var tsInterface =
                TsInterface.builder().name("ComplexData").fields(List.of(field)).build();

        String result = mapper.map(List.of(tsInterface));

        String expected = """
            export interface ComplexData {
              data: Record<string, number[]>;
            }

            """;
        assertThat(result).isEqualTo(expected);
    }
}
