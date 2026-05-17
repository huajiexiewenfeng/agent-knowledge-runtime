package io.github.xiewenfeng.agentknowledge.ingest.api;

public record ImportDocumentResponse(Long documentId, int chunkCount, String status) {
}
