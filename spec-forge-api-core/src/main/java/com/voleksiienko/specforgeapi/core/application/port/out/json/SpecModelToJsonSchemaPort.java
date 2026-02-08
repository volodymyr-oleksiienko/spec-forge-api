package com.voleksiienko.specforgeapi.core.application.port.out.json;

import com.voleksiienko.specforgeapi.core.domain.model.spec.SpecModel;

public interface SpecModelToJsonSchemaPort {

    String map(SpecModel specModel);
}
