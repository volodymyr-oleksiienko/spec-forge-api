package com.voleksiienko.specforgeapi.core.application.service.java;

import com.voleksiienko.specforgeapi.core.application.annotation.Component;
import com.voleksiienko.specforgeapi.core.application.service.java.inner.JavaClassDeduplicator;
import com.voleksiienko.specforgeapi.core.application.service.java.inner.JavaClassFactory;
import com.voleksiienko.specforgeapi.core.application.service.java.inner.type.JavaMappingContext;
import com.voleksiienko.specforgeapi.core.domain.model.config.JavaConfig;
import com.voleksiienko.specforgeapi.core.domain.model.java.JavaClass;
import com.voleksiienko.specforgeapi.core.domain.model.java.JavaType;
import com.voleksiienko.specforgeapi.core.domain.model.spec.SpecModel;
import java.util.ArrayList;
import java.util.List;

@Component
public class SpecModelToJavaModelMapper {

    private final JavaClassFactory javaClassFactory;
    private final JavaClassDeduplicator javaClassDeduplicator;

    public SpecModelToJavaModelMapper(JavaClassFactory javaClassFactory, JavaClassDeduplicator javaClassDeduplicator) {
        this.javaClassFactory = javaClassFactory;
        this.javaClassDeduplicator = javaClassDeduplicator;
    }

    public JavaClass map(SpecModel specModel, JavaConfig config) {
        List<JavaType> nestedClassesAccumulator = new ArrayList<>();
        String rootClassName = config.base().naming().className();
        JavaMappingContext context =
                new JavaMappingContext(rootClassName, nestedClassesAccumulator, config, false, false);
        JavaClass mainClass = javaClassFactory.mapToClass(rootClassName, specModel.getProperties(), context);
        List<JavaType> distinctNested = javaClassDeduplicator.deduplicate(nestedClassesAccumulator);
        boolean isRecord =
                JavaConfig.Structure.Type.RECORD == config.structure().type();
        return isRecord
                ? JavaClass.createRecord(
                        mainClass.getName(),
                        mainClass.getAnnotations(),
                        mainClass.getFields(),
                        mainClass.getNestedClasses())
                : JavaClass.createClass(
                        mainClass.getName(), mainClass.getAnnotations(), mainClass.getFields(), distinctNested);
    }
}
