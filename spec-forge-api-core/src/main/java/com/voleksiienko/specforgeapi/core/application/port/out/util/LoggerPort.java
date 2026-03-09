package com.voleksiienko.specforgeapi.core.application.port.out.util;

public interface LoggerPort {

    void logError(Throwable t, String message);
}
