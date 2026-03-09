package com.voleksiienko.specforgeapi.infra.adapter.out.util;

import com.voleksiienko.specforgeapi.core.application.port.out.util.LoggerPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LoggerAdapter implements LoggerPort {

    @Override
    public void logError(Throwable t, String message) {
        log.error(message, t);
    }
}
