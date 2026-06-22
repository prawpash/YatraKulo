package com.ekapasha.shared.logging;

import java.util.Map;

public record LogEvent(
    LogEventName eventName,
    String message,
    Long durationMs,
    Map<String, Object> metadata,
    Throwable error
) {
    public static LogEventBuilder builder(String message) {
        return new LogEventBuilder(message);
    }
}
