package io.github.xiewenfeng.agentknowledge.query.application;

import io.github.xiewenfeng.agentknowledge.common.error.ErrorCode;
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

    public QueryKnowledgeService(KeywordChunkRetriever retriever,
                                 CitationBuilder citationBuilder,
                                 AnswerComposer answerComposer) {
        this.retriever = retriever;
        this.citationBuilder = citationBuilder;
        this.answerComposer = answerComposer;
    }

    @Transactional(readOnly = true)
    public KnowledgeQueryResponse query(KnowledgeQueryRequest request) {
        if (request.query() == null || request.query().isBlank()) {
            throw new IllegalArgumentException(ErrorCode.QUERY_EMPTY.name());
        }

        List<RetrievedChunk> hits = retriever.retrieve(
                request.query(),
                request.effectiveTopK(),
                request.metadataFilter()
        );
        List<Citation> citations = hits.stream()
                .map(hit -> citationBuilder.from(hit.chunk()))
                .toList();
        return new KnowledgeQueryResponse(answerComposer.compose(request.query(), hits), citations, null);
    }
}
