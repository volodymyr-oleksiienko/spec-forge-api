package com.voleksiienko.specforgeapi.infra.adapter.out.ts;

import com.voleksiienko.specforgeapi.core.application.port.out.ts.TsModelToTsCodePort;
import com.voleksiienko.specforgeapi.core.domain.model.ts.*;
import com.voleksiienko.specforgeapi.infra.adapter.out.ts.inner.TsCodeWriter;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class TsModelToTsCodeAdapter implements TsModelToTsCodePort {

    @Override
    public String map(List<TsDeclaration> tsDeclarations) {
        TsCodeWriter writer = new TsCodeWriter();
        tsDeclarations.forEach(tsDeclaration -> {
            switch (tsDeclaration) {
                case TsInterface i -> writer.writeInterface(i);
                case TsTypeAlias t -> writer.writeTypeAlias(t);
                case TsEnum e -> writer.writeEnum(e);
                case TsUnionType u -> writer.writeUnion(u);
            }
        });
        return writer.toString();
    }
}
