package com.voleksiienko.specforgeapi.infra.adapter.in.web.mapper;

import static com.voleksiienko.specforgeapi.infra.TestHelper.readResource;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.voleksiienko.specforgeapi.core.application.port.in.artifact.result.ArtifactsResult;
import com.voleksiienko.specforgeapi.core.domain.model.conversion.Warning;
import com.voleksiienko.specforgeapi.core.domain.model.error.DomainErrorCode;
import com.voleksiienko.specforgeapi.core.domain.model.spec.SpecModel;
import com.voleksiienko.specforgeapi.core.domain.model.spec.SpecProperty;
import com.voleksiienko.specforgeapi.core.domain.model.spec.type.*;
import com.voleksiienko.specforgeapi.infra.adapter.in.web.dto.response.ArtifactsResponse;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import tools.jackson.databind.json.JsonMapper;

class ResponseMapperTest {

    private final ResponseMapper mapper = new ResponseMapper();
    private final JsonMapper objectMapper = JsonMapper.builder()
            .changeDefaultPropertyInclusion(inc -> inc.withValueInclusion(JsonInclude.Include.NON_EMPTY))
            .build();

    @Test
    void shouldSerializeToExpectedJsonContract() throws JSONException {
        ArtifactsResult domainResult = createArtifactsResult();

        ArtifactsResponse dto = mapper.map(domainResult);

        String expectedJson = readResource("/response-mapper-test/expected-artifacts-response.json");
        String actualJson = objectMapper.writeValueAsString(dto);
        JSONAssert.assertEquals(expectedJson, actualJson, JSONCompareMode.LENIENT);
    }

    private ArtifactsResult createArtifactsResult() {
        List<SpecProperty> properties = List.of(
                SpecProperty.builder()
                        .name("is_active")
                        .description("Basic Boolean")
                        .required(true)
                        .type(new BooleanSpecType())
                        .build(),
                SpecProperty.builder()
                        .name("retry_count")
                        .description("Basic Integer")
                        .required(true)
                        .type(IntegerSpecType.builder().minimum(0L).maximum(10L).build())
                        .build(),
                SpecProperty.builder()
                        .name("score")
                        .description("Basic Double")
                        .required(false)
                        .type(DoubleSpecType.builder()
                                .minimum(0.0)
                                .maximum(100.0)
                                .build())
                        .build(),
                SpecProperty.builder()
                        .name("money")
                        .description("Basic Decimal")
                        .required(false)
                        .type(DecimalSpecType.builder()
                                .minimum(BigDecimal.ZERO)
                                .maximum(new BigDecimal("100000"))
                                .scale(2)
                                .build())
                        .build(),
                SpecProperty.builder()
                        .name("dob")
                        .description("Date Type")
                        .required(false)
                        .type(DateSpecType.builder().format("yyyy-MM-dd").build())
                        .build(),
                SpecProperty.builder()
                        .name("meeting_time")
                        .description("Time Type")
                        .required(false)
                        .type(TimeSpecType.builder().format("HH:mm").build())
                        .build(),
                SpecProperty.builder()
                        .name("meta_data")
                        .description("Object Type")
                        .required(false)
                        .type(ObjectSpecType.builder()
                                .children(List.of(SpecProperty.builder()
                                        .name("created_at")
                                        .description("DateTime Type")
                                        .required(false)
                                        .type(DateTimeSpecType.builder()
                                                .format("yyyy-MM-dd HH:mm")
                                                .build())
                                        .build()))
                                .build())
                        .build(),
                SpecProperty.builder()
                        .name("roles")
                        .description("List Type")
                        .required(false)
                        .type(ListSpecType.builder()
                                .minItems(0)
                                .maxItems(10)
                                .valueType(EnumSpecType.builder()
                                        .values(Set.of("ADMIN", "USER"))
                                        .build())
                                .build())
                        .build(),
                SpecProperty.builder()
                        .name("attributes")
                        .description("Map Type")
                        .required(false)
                        .type(MapSpecType.builder()
                                .keyType(StringSpecType.builder()
                                        .minLength(3)
                                        .maxLength(20)
                                        .pattern("[a-z]+")
                                        .build())
                                .valueType(StringSpecType.builder()
                                        .minLength(5)
                                        .maxLength(50)
                                        .format(StringSpecType.StringTypeFormat.EMAIL)
                                        .build())
                                .build())
                        .build());

        SpecModel specModel = SpecModel.builder()
                .wrapperType(SpecModel.WrapperType.OBJECT)
                .properties(properties)
                .build();

        return new ArtifactsResult(
                specModel, List.of(new Warning("Something happened", DomainErrorCode.SPEC_VALIDATION_FAILED)));
    }
}
