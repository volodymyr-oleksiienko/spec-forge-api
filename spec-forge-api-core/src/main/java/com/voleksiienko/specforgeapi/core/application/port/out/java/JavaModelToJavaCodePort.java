package com.voleksiienko.specforgeapi.core.application.port.out.java;

import com.voleksiienko.specforgeapi.core.domain.model.java.JavaClass;

public interface JavaModelToJavaCodePort {

    String map(JavaClass javaClass);
}
