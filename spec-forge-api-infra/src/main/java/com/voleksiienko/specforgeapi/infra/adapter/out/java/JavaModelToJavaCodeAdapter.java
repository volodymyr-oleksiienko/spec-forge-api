package com.voleksiienko.specforgeapi.infra.adapter.out.java;

import static com.voleksiienko.specforgeapi.core.common.Asserts.*;

import com.palantir.javapoet.*;
import com.voleksiienko.specforgeapi.core.application.exception.ConversionException;
import com.voleksiienko.specforgeapi.core.application.port.out.java.JavaModelToJavaCodePort;
import com.voleksiienko.specforgeapi.core.domain.model.java.*;
import com.voleksiienko.specforgeapi.infra.adapter.in.web.dto.response.ApiErrorCode;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import javax.lang.model.element.Modifier;
import org.springframework.stereotype.Component;

@Component
public class JavaModelToJavaCodeAdapter implements JavaModelToJavaCodePort {

    @Override
    public String map(JavaClass rootClass) {
        TypeSpec typeSpec = buildTypeSpec(rootClass, true);
        JavaFile javaFile =
                JavaFile.builder("", typeSpec).skipJavaLangImports(true).build();
        return javaFile.toString();
    }

    private TypeSpec buildTypeSpec(JavaType javaType, boolean isRoot) {
        TypeSpec.Builder builder;

        switch (javaType) {
            case JavaEnum javaEnum -> {
                builder = TypeSpec.enumBuilder(javaType.getName());
                addEnumValues(javaEnum, builder);
                addEnumFields(javaEnum, builder);
            }
            case JavaClass javaClass -> {
                if (javaClass.isRecord()) {
                    builder = TypeSpec.recordBuilder(javaType.getName());
                    addRecordFields(javaClass, builder);
                } else {
                    builder = TypeSpec.classBuilder(javaType.getName());
                    addClassFields(javaType, builder);
                }
                addNestedJavaTypes(javaClass, builder);
            }
        }

        builder.addModifiers(Modifier.PUBLIC);
        if (!isRoot) {
            builder.addModifiers(Modifier.STATIC);
        }

        if (isNotEmpty(javaType.getAnnotations())) {
            javaType.getAnnotations().forEach(annotation -> builder.addAnnotation(resolveAnnotation(annotation)));
        }

        return builder.build();
    }

    private void addEnumValues(JavaEnum javaEnum, TypeSpec.Builder builder) {
        javaEnum.getConstants().forEach(constant -> {
            if (isNotEmpty(constant.getArguments())) {
                String argsFormat = String.join(
                        ", ", Collections.nCopies(constant.getArguments().size(), "$L"));
                builder.addEnumConstant(
                        constant.getName(),
                        TypeSpec.anonymousClassBuilder(
                                        argsFormat, constant.getArguments().toArray())
                                .build());
            } else {
                builder.addEnumConstant(constant.getName());
            }
        });
    }

    private void addEnumFields(JavaEnum javaEnum, TypeSpec.Builder builder) {
        Optional.ofNullable(javaEnum.getFields())
                .ifPresent(javaFields -> javaFields.forEach(javaField -> {
                    FieldSpec.Builder fieldBuilder = FieldSpec.builder(
                            resolveType(javaField.getType()), javaField.getName(), Modifier.PRIVATE, Modifier.FINAL);
                    if (isNotEmpty(javaField.getAnnotations())) {
                        javaField
                                .getAnnotations()
                                .forEach(annotation -> fieldBuilder.addAnnotation(resolveAnnotation(annotation)));
                    }
                    builder.addField(fieldBuilder.build());
                }));
    }

    private void addRecordFields(JavaClass javaClass, TypeSpec.Builder builder) {
        MethodSpec.Builder constructorBuilder = MethodSpec.constructorBuilder();
        javaClass.getFields().forEach(javaField -> {
            ParameterSpec.Builder parameter =
                    ParameterSpec.builder(resolveType(javaField.getType()), javaField.getName());
            if (isNotEmpty(javaField.getAnnotations())) {
                javaField
                        .getAnnotations()
                        .forEach(annotation -> parameter.addAnnotation(resolveAnnotation(annotation)));
            }
            constructorBuilder.addParameter(parameter.build());
        });
        builder.recordConstructor(constructorBuilder.build());
    }

    private void addClassFields(JavaType javaType, TypeSpec.Builder builder) {
        javaType.getFields().forEach(javaField -> {
            FieldSpec.Builder fieldBuilder =
                    FieldSpec.builder(resolveType(javaField.getType()), javaField.getName(), Modifier.PRIVATE);
            if (isNotEmpty(javaField.getAnnotations())) {
                javaField
                        .getAnnotations()
                        .forEach(annotation -> fieldBuilder.addAnnotation(resolveAnnotation(annotation)));
            }
            builder.addField(fieldBuilder.build());
        });
    }

    private void addNestedJavaTypes(JavaClass javaClass, TypeSpec.Builder builder) {
        if (isNotEmpty(javaClass.getNestedClasses())) {
            javaClass.getNestedClasses().forEach(nestedClass -> builder.addType(buildTypeSpec(nestedClass, false)));
        }
    }

    private TypeName resolveType(TypeReference typeRef) {
        if (typeRef.isPrimitive()) {
            return getPrimitiveType(typeRef.getSimpleName());
        }
        ClassName className = isNotBlank(typeRef.getPackageName())
                ? ClassName.get(typeRef.getPackageName(), typeRef.getSimpleName())
                : ClassName.bestGuess(typeRef.getSimpleName());
        if (isEmpty(typeRef.getGenericArguments())) {
            return className;
        }
        TypeName[] generics =
                typeRef.getGenericArguments().stream().map(this::resolveType).toArray(TypeName[]::new);
        return ParameterizedTypeName.get(className, generics);
    }

    private TypeName getPrimitiveType(String typeName) {
        return switch (typeName) {
            case "void" -> TypeName.VOID;
            case "boolean" -> TypeName.BOOLEAN;
            case "byte" -> TypeName.BYTE;
            case "short" -> TypeName.SHORT;
            case "int" -> TypeName.INT;
            case "long" -> TypeName.LONG;
            case "char" -> TypeName.CHAR;
            case "float" -> TypeName.FLOAT;
            case "double" -> TypeName.DOUBLE;
            case null, default ->
                throw new ConversionException("Unknown primitive type: %s".formatted(typeName), ApiErrorCode.INTERNAL);
        };
    }

    private AnnotationSpec resolveAnnotation(JavaAnnotation annotation) {
        ClassName annClass = ClassName.get(annotation.getPackageName(), annotation.getSimpleName());
        AnnotationSpec.Builder builder = AnnotationSpec.builder(annClass);
        if (Objects.nonNull(annotation.getAttributes())) {
            annotation.getAttributes().forEach((k, v) -> builder.addMember(k, "$L", v));
        }
        return builder.build();
    }
}
