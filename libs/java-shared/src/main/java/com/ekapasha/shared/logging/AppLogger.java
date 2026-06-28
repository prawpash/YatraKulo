package com.ekapasha.shared.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.HashMap;
import java.util.Map;

// Framework-agnostic. Relies on SLF4J MDC which is populated internally.
public class AppLogger {
    private final Logger logger;

    public AppLogger(Class<?> clazz) {
        this.logger = LoggerFactory.getLogger(clazz);
    }

    public void info(String message) {
        info(LogEvent.builder(message).build());
    }

    public void info(LogEvent event) {
        if (logger.isInfoEnabled()) {
            withMdc(event, () -> logger.info(event.message(), event.error()));
        }
    }

    public void warn(String message) {
        warn(LogEvent.builder(message).build());
    }

    public void warn(LogEvent event) {
        if (logger.isWarnEnabled()) {
            withMdc(event, () -> logger.warn(event.message(), event.error()));
        }
    }

    public void debug(String message) {
        debug(LogEvent.builder(message).build());
    }

    public void debug(LogEvent event) {
        if (logger.isDebugEnabled()) {
            withMdc(event, () -> logger.debug(event.message(), event.error()));
        }
    }

    public void error(String message) {
        error(LogEvent.builder(message).build());
    }

    public void error(String message, Throwable error) {
        error(LogEvent.builder(message).error(error).build());
    }

    public void error(LogEvent event) {
        if (logger.isErrorEnabled()) {
            withMdc(event, () -> logger.error(event.message(), event.error()));
        }
    }

    private void withMdc(LogEvent event, Runnable logAction) {
        Map<String, String> mdcContext = buildMdcContext(event);
        mdcContext.forEach(MDC::put);
        try {
            logAction.run();
        } finally {
            mdcContext.keySet().forEach(MDC::remove);
        }
    }

    private Map<String, String> buildMdcContext(LogEvent event) {
        Map<String, String> entries = new HashMap<>();
        if (event.eventName() != null) {
            entries.put("eventName", event.eventName().name());
        }
        if (event.durationMs() != null) {
            entries.put("durationMs", String.valueOf(event.durationMs()));
        }
        if (event.metadata() != null) {
            event.metadata().forEach((k, v) -> entries.put(k, String.valueOf(v)));
        }
        return entries;
    }
}
