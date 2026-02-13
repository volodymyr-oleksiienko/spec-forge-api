package com.voleksiienko.specforgeapi.infra.config;

import com.voleksiienko.specforgeapi.core.application.annotation.Component;
import com.voleksiienko.specforgeapi.core.application.annotation.UseCase;
import com.voleksiienko.specforgeapi.core.application.service.java.inner.type.TypeReferenceCreator;
import com.voleksiienko.specforgeapi.core.application.service.java.inner.type.TypeReferenceCreatorFacade;
import java.util.List;
import org.springframework.context.annotation.*;

@Configuration
@ComponentScan(
        basePackages = "com.voleksiienko.specforgeapi.core",
        includeFilters = {
            @ComponentScan.Filter(
                    type = FilterType.ANNOTATION,
                    classes = {UseCase.class, Component.class})
        })
public class DomainConfiguration {

    @Bean
    public TypeReferenceCreatorFacade typeReferenceCreatorFacade(@Lazy List<TypeReferenceCreator> strategies) {
        return new TypeReferenceCreatorFacade(strategies);
    }
}
