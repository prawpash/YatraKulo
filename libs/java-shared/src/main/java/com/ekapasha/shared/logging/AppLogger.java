package com.ekapasha.shared.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.logstash.logback.argument.StructuredArguments;

import java.util.HashMap;
import java.util.Map;

// Framework-agnostic. Relies on SLF4J MDC which is populated externally.
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
            logger.info(event.message(), StructuredArguments.entries(buildEntries(event)), event.error());
        }
    }

    public void warn(String message) {
        warn(LogEvent.builder(message).build());
    }

    public void warn(LogEvent event) {
        if (logger.isWarnEnabled()) {
            logger.warn(event.message(), StructuredArguments.entries(buildEntries(event)), event.error());
        }
    }

    public void debug(String message) {
        debug(LogEvent.builder(message).build());
    }

    public void debug(LogEvent event) {
        if (logger.isDebugEnabled()) {
            logger.debug(event.message(), StructuredArguments.entries(buildEntries(event)), event.error());
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
            logger.error(event.message(), StructuredArguments.entries(buildEntries(event)), event.error());
        }
    }

    private Map<String, Object> buildEntries(LogEvent event) {
        Map<String, Object> entries = new HashMap<>();
        if (event.eventName() != null) {
            entries.put("eventName", event.eventName().name());
        }
        if (event.durationMs() != null) {
            entries.put("durationMs", event.durationMs());
        }
        if (event.metadata() != null) {
            entries.putAll(event.metadata());
        }
        return entries;
    }
}
