package io.github.xiewenfeng.agentknowledge.common.trace;

import java.time.Instant;
import java.util.List;

public record TraceResponse(
        String traceId,
        String query,
        List<Long> retrievedChunkIds,
        List<TraceEvent> events,
        long latencyMs,
        Instant createdAt
) {
}
