package io.github.xiewenfeng.agentknowledge.common.error;

import java.time.Instant;

public record ApiErrorResponse(
        ErrorCode errorCode,
        String message,
        String traceId,
        Instant timestamp
) {
    public static ApiErrorResponse of(ErrorCode errorCode, String message, String traceId) {
        return new ApiErrorResponse(errorCode, message, traceId, Instant.now());
    }
}
