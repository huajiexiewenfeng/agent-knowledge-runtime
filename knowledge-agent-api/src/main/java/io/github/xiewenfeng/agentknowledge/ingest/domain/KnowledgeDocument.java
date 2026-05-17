package io.github.xiewenfeng.agentknowledge.ingest.domain;

import java.time.Instant;

public record KnowledgeDocument(
        Long id,
        String sourcePath,
        String title,
        String contentHash,
        Instant importedAt,
        Instant updatedAt,
        DocumentStatus status
) {
}
