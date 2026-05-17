package io.github.xiewenfeng.agentknowledge.common.trace;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.xiewenfeng.agentknowledge.common.error.ErrorCode;
import io.github.xiewenfeng.agentknowledge.common.error.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class TraceService {
    private final TraceRepository traceRepository;
    private final ObjectMapper objectMapper;

    public TraceService(TraceRepository traceRepository, ObjectMapper objectMapper) {
        this.traceRepository = traceRepository;
        this.objectMapper = objectMapper;
    }

    public String record(String query, List<Long> retrievedChunkIds, List<TraceEvent> events, long latencyMs) {
        String traceId = "trace_" + UUID.randomUUID();
        traceRepository.save(new TraceEntity(
                traceId,
                query,
                toJson(retrievedChunkIds),
                toJson(events),
                latencyMs,
                Instant.now()
        ));
        return traceId;
    }

    public TraceResponse getTrace(String traceId) {
        TraceEntity entity = traceRepository.findById(traceId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.TRACE_NOT_FOUND, "Trace not found: " + traceId));
        return new TraceResponse(
                entity.getTraceId(),
                entity.getQueryText(),
                fromJson(entity.getRetrievedChunkIds(), new TypeReference<List<Long>>() {}),
                fromJson(entity.getEventsJson(), new TypeReference<List<TraceEvent>>() {}),
                entity.getLatencyMs(),
                entity.getCreatedAt()
        );
    }

    private String toJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Unable to serialize trace value", exception);
        }
    }

    private <T> T fromJson(String json, TypeReference<T> typeReference) {
        try {
            return objectMapper.readValue(json, typeReference);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Unable to deserialize trace value", exception);
        }
    }
}
