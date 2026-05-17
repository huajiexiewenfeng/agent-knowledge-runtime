package io.github.xiewenfeng.agentknowledge.ingest.domain;

import java.time.Instant;

public record DocumentChunk(
        Long id,
        Long documentId,
        String sourcePath,
        String heading,
        int chunkIndex,
        String content,
        String contentHash,
        String metadataJson,
        Instant createdAt
) {
}
