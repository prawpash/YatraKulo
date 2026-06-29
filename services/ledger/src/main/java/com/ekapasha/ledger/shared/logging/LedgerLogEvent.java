package com.ekapasha.ledger.shared.logging;

import com.ekapasha.shared.logging.LogEventName;

public enum LedgerLogEvent implements LogEventName {
    JWT_CONVERSION_SUCCESS,
    JWT_CONVERSION_FAILED,
    MISSING_WORKSPACE_ID
}
