package com.voleksiienko.specforgeapi.core.application.service.ts;

import com.voleksiienko.specforgeapi.core.application.annotation.Component;
import com.voleksiienko.specforgeapi.core.application.service.ts.inner.TsDeclarationDeduplicator;
import com.voleksiienko.specforgeapi.core.application.service.ts.inner.TsDeclarationFactory;
import com.voleksiienko.specforgeapi.core.application.service.ts.inner.type.TsMappingContext;
import com.voleksiienko.specforgeapi.core.domain.model.config.TypeScriptConfig;
import com.voleksiienko.specforgeapi.core.domain.model.spec.SpecModel;
import com.voleksiienko.specforgeapi.core.domain.model.ts.TsDeclaration;
import java.util.ArrayList;
import java.util.List;

@Component
public class SpecModelToTsModelMapper {

    private final TsDeclarationFactory declarationFactory;
    private final TsDeclarationDeduplicator deduplicator;

    public SpecModelToTsModelMapper(TsDeclarationFactory declarationFactory, TsDeclarationDeduplicator deduplicator) {
        this.declarationFactory = declarationFactory;
        this.deduplicator = deduplicator;
    }

    public List<TsDeclaration> map(SpecModel specModel, TypeScriptConfig config) {
        List<TsDeclaration> declarations = new ArrayList<>();
        String rootName = config.base().naming().className();
        TsMappingContext context = new TsMappingContext(rootName, declarations, config, false);
        TsDeclaration rootDeclaration = declarationFactory.createObject(rootName, specModel.getProperties(), context);
        declarations.add(rootDeclaration);
        return deduplicator.deduplicate(declarations);
    }
}
