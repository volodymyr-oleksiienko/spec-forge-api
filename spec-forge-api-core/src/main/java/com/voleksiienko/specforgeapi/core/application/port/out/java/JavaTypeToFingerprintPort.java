package com.voleksiienko.specforgeapi.core.application.port.out.java;

import com.voleksiienko.specforgeapi.core.domain.model.java.JavaType;

public interface JavaTypeToFingerprintPort {

    String map(JavaType type);
}
