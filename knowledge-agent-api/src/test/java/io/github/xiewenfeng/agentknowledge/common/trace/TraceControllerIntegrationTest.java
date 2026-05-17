package io.github.xiewenfeng.agentknowledge.common.trace;

import io.github.xiewenfeng.agentknowledge.ingest.domain.DocumentChunk;
import io.github.xiewenfeng.agentknowledge.ingest.infrastructure.ChunkRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TraceControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ChunkRepository chunkRepository;

    @BeforeEach
    void cleanDatabase() {
        chunkRepository.deleteAll();
    }

    @Test
    void queryCreatesRetrievableTrace() throws Exception {
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

        String response = mockMvc.perform(post("/api/v1/query")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "query": "memory commit",
                                  "topK": 3,
                                  "metadataFilter": {
                                    "project": "agent-global-context"
                                  }
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.traceId").isString())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String traceId = response.replaceAll(".*\\\"traceId\\\":\\\"([^\\\"]+)\\\".*", "$1");
        assertThat(traceId).isNotBlank();

        mockMvc.perform(get("/api/v1/traces/" + traceId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.traceId").value(traceId))
                .andExpect(jsonPath("$.query").value("memory commit"))
                .andExpect(jsonPath("$.retrievedChunkIds").isArray());
    }
}
