package io.github.xiewenfeng.agentknowledge.query.domain;

public record Citation(
        Long chunkId,
        Long documentId,
        String sourcePath,
        String heading,
        int chunkIndex,
        String snippet
) {
}
