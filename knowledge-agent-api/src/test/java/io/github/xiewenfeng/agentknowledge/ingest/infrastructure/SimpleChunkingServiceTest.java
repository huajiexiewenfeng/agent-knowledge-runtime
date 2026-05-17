package io.github.xiewenfeng.agentknowledge.ingest.infrastructure;

import io.github.xiewenfeng.agentknowledge.ingest.domain.DocumentChunk;
import io.github.xiewenfeng.agentknowledge.ingest.domain.ParsedMarkdownSection;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SimpleChunkingServiceTest {

    @Test
    void createsChunksWithSourceHeadingAndIndex() {
        SimpleChunkingService service = new SimpleChunkingService();
        List<ParsedMarkdownSection> sections = List.of(
                new ParsedMarkdownSection("Commit Policy", "Explicit memory requests can usually be committed directly."),
                new ParsedMarkdownSection("Recall Policy", "Recall should load only relevant memory.")
        );

        List<DocumentChunk> chunks = service.createChunks(
                10L,
                "docs/architecture.zh.md",
                sections,
                "{\"project\":\"agent-global-context\"}"
        );

        assertThat(chunks).hasSize(2);
        assertThat(chunks.get(0).getDocumentId()).isEqualTo(10L);
        assertThat(chunks.get(0).getSourcePath()).isEqualTo("docs/architecture.zh.md");
        assertThat(chunks.get(0).getHeading()).isEqualTo("Commit Policy");
        assertThat(chunks.get(0).getChunkIndex()).isZero();
        assertThat(chunks.get(0).getContentHash()).hasSize(64);
        assertThat(chunks.get(0).getMetadataJson()).contains("agent-global-context");
    }
}
