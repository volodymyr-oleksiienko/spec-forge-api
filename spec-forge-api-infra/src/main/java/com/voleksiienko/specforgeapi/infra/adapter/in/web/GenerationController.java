package com.voleksiienko.specforgeapi.infra.adapter.in.web;

import com.voleksiienko.specforgeapi.core.application.port.in.artifact.GenerateArtifactsUseCase;
import com.voleksiienko.specforgeapi.core.application.port.in.artifact.result.ArtifactsResult;
import com.voleksiienko.specforgeapi.infra.adapter.in.web.dto.request.GenerateFromRawRequest;
import com.voleksiienko.specforgeapi.infra.adapter.in.web.dto.response.ArtifactsResponse;
import com.voleksiienko.specforgeapi.infra.adapter.in.web.mapper.RequestMapper;
import com.voleksiienko.specforgeapi.infra.adapter.in.web.mapper.ResponseMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class GenerationController {

    private final GenerateArtifactsUseCase useCase;
    private final RequestMapper requestMapper;
    private final ResponseMapper responseMapper;

    @PostMapping("/artifacts/from-json-schema")
    public ArtifactsResponse generateFromJsonSchema(@RequestBody @Valid GenerateFromRawRequest request) {
        var command = requestMapper.toGenerateFromJsonSchemaCommand(request);
        ArtifactsResult result = useCase.generateFromJsonSchema(command);
        return responseMapper.map(result);
    }

    @PostMapping("/artifacts/from-json-sample")
    public ArtifactsResponse generateFromJsonSample(@RequestBody @Valid GenerateFromRawRequest request) {
        var command = requestMapper.toGenerateFromJsonSampleCommand(request);
        ArtifactsResult result = useCase.generateFromJsonSample(command);
        return responseMapper.map(result);
    }
}
