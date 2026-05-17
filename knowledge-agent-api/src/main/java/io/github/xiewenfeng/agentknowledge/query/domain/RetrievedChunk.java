package io.github.xiewenfeng.agentknowledge.query.domain;

import io.github.xiewenfeng.agentknowledge.ingest.domain.DocumentChunk;

public record RetrievedChunk(DocumentChunk chunk, int score) {
}
