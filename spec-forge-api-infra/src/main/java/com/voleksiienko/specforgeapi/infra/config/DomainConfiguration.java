package com.voleksiienko.specforgeapi.infra.config;

import com.voleksiienko.specforgeapi.core.application.annotation.Component;
import com.voleksiienko.specforgeapi.core.application.annotation.UseCase;
import com.voleksiienko.specforgeapi.core.application.service.java.inner.type.JavaTypeReferenceCreator;
import com.voleksiienko.specforgeapi.core.application.service.java.inner.type.JavaTypeReferenceCreatorFacade;
import com.voleksiienko.specforgeapi.core.application.service.ts.inner.type.TsTypeReferenceCreator;
import com.voleksiienko.specforgeapi.core.application.service.ts.inner.type.TsTypeReferenceCreatorFacade;
import java.util.List;
import org.springframework.context.annotation.*;

@Configuration(proxyBeanMethods = false)
@ComponentScan(
        basePackages = "com.voleksiienko.specforgeapi.core",
        includeFilters = {
            @ComponentScan.Filter(
                    type = FilterType.ANNOTATION,
                    classes = {UseCase.class, Component.class})
        })
public class DomainConfiguration {

    @Bean
    public JavaTypeReferenceCreatorFacade javaTypeReferenceCreatorFacade(
            @Lazy List<JavaTypeReferenceCreator> strategies) {
        return new JavaTypeReferenceCreatorFacade(strategies);
    }

    @Bean
    public TsTypeReferenceCreatorFacade tsTypeReferenceCreatorFacade(@Lazy List<TsTypeReferenceCreator> strategies) {
        return new TsTypeReferenceCreatorFacade(strategies);
    }
}
