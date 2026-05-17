package io.github.xiewenfeng.agentknowledge.query.api;

import io.github.xiewenfeng.agentknowledge.query.domain.Citation;

import java.util.List;

public record KnowledgeQueryResponse(
        String answer,
        List<Citation> citations,
        String traceId
) {
}
