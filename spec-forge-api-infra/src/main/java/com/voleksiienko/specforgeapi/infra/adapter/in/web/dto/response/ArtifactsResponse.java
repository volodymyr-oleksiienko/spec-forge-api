package com.voleksiienko.specforgeapi.infra.adapter.in.web.dto.response;

import com.voleksiienko.specforgeapi.infra.adapter.in.web.dto.SpecModelDto;
import java.util.List;

public record ArtifactsResponse(SpecModelDto specModel, String jsonSample, String jsonSchema, List<Warning> warnings) {

    public record Warning(String devMessage, String errorCode) {}
}
