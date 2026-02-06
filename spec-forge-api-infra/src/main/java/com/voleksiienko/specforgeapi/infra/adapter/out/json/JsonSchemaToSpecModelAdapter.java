package com.voleksiienko.specforgeapi.infra.adapter.out.json;

import static com.voleksiienko.specforgeapi.core.domain.model.error.JsonMappingErrorCode.JSON_SCHEMA_CHILDREN_MAPPING_FAILED;
import static com.voleksiienko.specforgeapi.core.domain.model.error.JsonMappingErrorCode.JSON_SCHEMA_PARSING_FAILED;

import com.voleksiienko.specforgeapi.core.application.exception.ConversionException;
import com.voleksiienko.specforgeapi.core.application.port.out.json.JsonSchemaToSpecModelPort;
import com.voleksiienko.specforgeapi.core.domain.model.conversion.ConversionResult;
import com.voleksiienko.specforgeapi.core.domain.model.spec.SpecModel;
import com.voleksiienko.specforgeapi.core.domain.model.spec.SpecProperty;
import com.voleksiienko.specforgeapi.core.domain.model.spec.type.SpecType;
import com.voleksiienko.specforgeapi.infra.adapter.out.json.inner.JsonSchemaParser;
import com.voleksiienko.specforgeapi.infra.adapter.out.json.inner.JsonSchemaReferenceResolver;
import com.voleksiienko.specforgeapi.infra.adapter.out.json.inner.ParsingContext;
import com.voleksiienko.specforgeapi.infra.adapter.out.json.inner.SpecPropertyMapper;
import com.voleksiienko.specforgeapi.infra.adapter.out.json.inner.type.SpecTypeCreatorFacade;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

@Service
@RequiredArgsConstructor
public class JsonSchemaToSpecModelAdapter implements JsonSchemaToSpecModelPort {

    private static final JsonMapper JSON_MAPPER = JsonMapper.builder().build();
    private final JsonSchemaReferenceResolver referenceResolver;
    private final JsonSchemaParser parser;
    private final SpecTypeCreatorFacade typeCreatorFacade;
    private final SpecPropertyMapper propertyMapper;

    @Override
    public ConversionResult map(String jsonSchema) {
        try {
            ParsingContext parsingContext = new ParsingContext();
            JsonNode rootNode = referenceResolver.resolveRefs(JSON_MAPPER.readTree(jsonSchema));
            SpecModel specModel = SpecModel.builder()
                    .wrapperType(parser.getWrapperType(rootNode))
                    .specProperties(mapChildren(rootNode, parsingContext))
                    .build();
            return new ConversionResult(specModel, parsingContext.getWarnings());
        } catch (ConversionException e) {
            throw e;
        } catch (Exception e) {
            throw new ConversionException("Failed to convert json schema to spec model", e, JSON_SCHEMA_PARSING_FAILED);
        }
    }

    private List<SpecProperty> mapChildren(JsonNode node, ParsingContext parsingContext) {
        List<JsonSchemaParser.ChildDefinition> childDefinitions = parser.extractChildren(node, parsingContext);
        return childDefinitions.stream()
                .map(def -> mapToChildSafely(parsingContext, def))
                .filter(Objects::nonNull)
                .toList();
    }

    private SpecProperty mapToChildSafely(ParsingContext parsingContext, JsonSchemaParser.ChildDefinition def) {
        try {
            SpecType specType = typeCreatorFacade.create(def.node(), parsingContext);
            return propertyMapper.mapToNode(
                    def, specType, specType.isObjectStructure() ? mapChildren(def.node(), parsingContext) : null);
        } catch (Exception e) {
            parsingContext.addWarning(
                    "Fatal error in children mapping: " + e.getMessage(), JSON_SCHEMA_CHILDREN_MAPPING_FAILED);
            return null;
        }
    }
}
