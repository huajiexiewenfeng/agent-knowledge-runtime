package io.github.xiewenfeng.agentknowledge.query.infrastructure;

import io.github.xiewenfeng.agentknowledge.ingest.domain.DocumentChunk;
import io.github.xiewenfeng.agentknowledge.query.domain.Citation;
import org.springframework.stereotype.Component;

@Component
public class CitationBuilder {

    public Citation from(DocumentChunk chunk) {
        return new Citation(
                chunk.getId(),
                chunk.getDocumentId(),
                chunk.getSourcePath(),
                chunk.getHeading(),
                chunk.getChunkIndex(),
                snippet(chunk.getContent())
        );
    }

    private String snippet(String content) {
        if (content.length() <= 180) {
            return content;
        }
        return content.substring(0, 177) + "...";
    }
}
