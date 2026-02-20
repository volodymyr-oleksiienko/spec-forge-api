package com.voleksiienko.specforgeapi.core.domain.model.conversion;

import com.voleksiienko.specforgeapi.core.domain.model.spec.SpecModel;
import java.util.List;

public record ConversionResult(SpecModel model, List<Warning> warnings) {}
