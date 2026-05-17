package io.github.xiewenfeng.agentknowledge.query.application;

import io.github.xiewenfeng.agentknowledge.common.error.ErrorCode;
import io.github.xiewenfeng.agentknowledge.common.trace.TraceEvent;
import io.github.xiewenfeng.agentknowledge.common.trace.TraceService;
import io.github.xiewenfeng.agentknowledge.query.api.KnowledgeQueryRequest;
import io.github.xiewenfeng.agentknowledge.query.api.KnowledgeQueryResponse;
import io.github.xiewenfeng.agentknowledge.query.domain.Citation;
import io.github.xiewenfeng.agentknowledge.query.domain.RetrievedChunk;
import io.github.xiewenfeng.agentknowledge.query.infrastructure.AnswerComposer;
import io.github.xiewenfeng.agentknowledge.query.infrastructure.CitationBuilder;
import io.github.xiewenfeng.agentknowledge.query.infrastructure.KeywordChunkRetriever;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class QueryKnowledgeService {
    private final KeywordChunkRetriever retriever;
    private final CitationBuilder citationBuilder;
    private final AnswerComposer answerComposer;
    private final TraceService traceService;

    public QueryKnowledgeService(KeywordChunkRetriever retriever,
                                 CitationBuilder citationBuilder,
                                 AnswerComposer answerComposer,
                                 TraceService traceService) {
        this.retriever = retriever;
        this.citationBuilder = citationBuilder;
        this.answerComposer = answerComposer;
        this.traceService = traceService;
    }

    @Transactional
    public KnowledgeQueryResponse query(KnowledgeQueryRequest request) {
        if (request.query() == null || request.query().isBlank()) {
            throw new IllegalArgumentException(ErrorCode.QUERY_EMPTY.name());
        }

        long startedAt = System.nanoTime();
        List<RetrievedChunk> hits = retriever.retrieve(
                request.query(),
                request.effectiveTopK(),
                request.metadataFilter()
        );
        List<Citation> citations = hits.stream()
                .map(hit -> citationBuilder.from(hit.chunk()))
                .toList();
        String answer = answerComposer.compose(request.query(), hits);
        long latencyMs = (System.nanoTime() - startedAt) / 1_000_000;

        List<Long> retrievedChunkIds = hits.stream()
                .map(hit -> hit.chunk().getId())
                .toList();
        String traceId = traceService.record(
                request.query(),
                retrievedChunkIds,
                List.of(
                        new TraceEvent("query.received", "topK=" + request.effectiveTopK()),
                        new TraceEvent("chunks.retrieved", "count=" + hits.size())
                ),
                latencyMs
        );

        return new KnowledgeQueryResponse(answer, citations, traceId);
    }
}
