package com.ekapasha.shared.logging;

import java.util.HashMap;
import java.util.Map;

public class LogEventBuilder {
    private final String message;
    private LogEventName eventName;
    private Long durationMs;
    private final Map<String, Object> metadata = new HashMap<>();
    private Throwable error;

    public LogEventBuilder(String message) {
        this.message = message;
    }

    public LogEventBuilder eventName(LogEventName eventName) {
        this.eventName = eventName;
        return this;
    }

    public LogEventBuilder durationMs(Long durationMs) {
        this.durationMs = durationMs;
        return this;
    }

    public LogEventBuilder metadata(Map<String, Object> metadata) {
        if (metadata != null) {
            this.metadata.putAll(metadata);
        }
        return this;
    }

    public LogEventBuilder metadata(String key, Object value) {
        this.metadata.put(key, value);
        return this;
    }

    public LogEventBuilder error(Throwable error) {
        this.error = error;
        return this;
    }

    public LogEvent build() {
        return new LogEvent(eventName, message, durationMs, metadata, error);
    }
}
