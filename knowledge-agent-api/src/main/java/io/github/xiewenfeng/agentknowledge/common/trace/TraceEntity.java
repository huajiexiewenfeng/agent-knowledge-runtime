package io.github.xiewenfeng.agentknowledge.common.trace;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "query_trace")
public class TraceEntity {
    @Id
    private String traceId;

    @Column(nullable = false, length = 2000)
    private String queryText;

    @Column(nullable = false, length = 2000)
    private String retrievedChunkIds;

    @Column(nullable = false, length = 4000)
    private String eventsJson;

    @Column(nullable = false)
    private long latencyMs;

    @Column(nullable = false)
    private Instant createdAt;

    protected TraceEntity() {
    }

    public TraceEntity(String traceId, String queryText, String retrievedChunkIds,
                       String eventsJson, long latencyMs, Instant createdAt) {
        this.traceId = traceId;
        this.queryText = queryText;
        this.retrievedChunkIds = retrievedChunkIds;
        this.eventsJson = eventsJson;
        this.latencyMs = latencyMs;
        this.createdAt = createdAt;
    }

    public String getTraceId() {
        return traceId;
    }

    public String getQueryText() {
        return queryText;
    }

    public String getRetrievedChunkIds() {
        return retrievedChunkIds;
    }

    public String getEventsJson() {
        return eventsJson;
    }

    public long getLatencyMs() {
        return latencyMs;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
