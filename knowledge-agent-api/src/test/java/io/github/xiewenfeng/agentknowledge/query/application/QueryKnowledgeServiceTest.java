package io.github.xiewenfeng.agentknowledge.query.application;

import io.github.xiewenfeng.agentknowledge.ingest.domain.DocumentChunk;
import io.github.xiewenfeng.agentknowledge.ingest.infrastructure.ChunkRepository;
import io.github.xiewenfeng.agentknowledge.query.api.KnowledgeQueryRequest;
import io.github.xiewenfeng.agentknowledge.query.api.KnowledgeQueryResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class QueryKnowledgeServiceTest {

    @Autowired
    private ChunkRepository chunkRepository;

    @Autowired
    private QueryKnowledgeService queryKnowledgeService;

    @BeforeEach
    void cleanDatabase() {
        chunkRepository.deleteAll();
    }

    @Test
    void retrievesMatchingChunksAndBuildsCitations() {
        chunkRepository.save(new DocumentChunk(
                1L,
                "docs/architecture.zh.md",
                "Commit Policy",
                0,
                "Explicit memory requests can usually be committed directly after confirmation.",
                "hashhashhashhashhashhashhashhashhashhashhashhashhashhashhashhash",
                "{\"project\":\"agent-global-context\"}",
                Instant.now()
        ));
        chunkRepository.save(new DocumentChunk(
                1L,
                "docs/architecture.zh.md",
                "Recall Policy",
                1,
                "Recall should load only relevant memory.",
                "hashhashhashhashhashhashhashhashhashhashhashhashhashhashhashhas1",
                "{\"project\":\"agent-global-context\"}",
                Instant.now()
        ));

        KnowledgeQueryResponse response = queryKnowledgeService.query(new KnowledgeQueryRequest(
                "memory commit rules",
                3,
                Map.of("project", "agent-global-context")
        ));

        assertThat(response.answer()).contains("Commit Policy");
        assertThat(response.citations()).isNotEmpty();
        assertThat(response.citations().get(0).sourcePath()).isEqualTo("docs/architecture.zh.md");
        assertThat(response.citations().get(0).heading()).isEqualTo("Commit Policy");
        assertThat(response.traceId()).startsWith("trace_");
    }
}
