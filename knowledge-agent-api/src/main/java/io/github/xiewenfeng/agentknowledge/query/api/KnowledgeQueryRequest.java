package io.github.xiewenfeng.agentknowledge.query.api;

import jakarta.validation.constraints.NotBlank;

import java.util.Map;

public record KnowledgeQueryRequest(
        @NotBlank String query,
        Integer topK,
        Map<String, String> metadataFilter
) {
    public int effectiveTopK() {
        if (topK == null || topK < 1) {
            return 5;
        }
        return Math.min(topK, 20);
    }
}
