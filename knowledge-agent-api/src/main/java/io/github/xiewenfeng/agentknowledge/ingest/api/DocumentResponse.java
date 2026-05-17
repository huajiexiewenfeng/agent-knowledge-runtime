package io.github.xiewenfeng.agentknowledge.ingest.api;

import io.github.xiewenfeng.agentknowledge.ingest.domain.KnowledgeDocument;

import java.time.Instant;

public record DocumentResponse(
        Long documentId,
        String sourcePath,
        String title,
        String status,
        int chunkCount,
        Instant importedAt,
        Instant updatedAt
) {
    public static DocumentResponse from(KnowledgeDocument document, int chunkCount) {
        return new DocumentResponse(
                document.getId(),
                document.getSourcePath(),
                document.getTitle(),
                document.getStatus().name(),
                chunkCount,
                document.getImportedAt(),
                document.getUpdatedAt()
        );
    }
}
