package com.voleksiienko.specforgeapi.core.application.port.out.ts;

import com.voleksiienko.specforgeapi.core.domain.model.ts.TsDeclaration;
import java.util.List;

public interface TsModelToTsCodePort {

    String map(List<TsDeclaration> tsDeclarations);
}
