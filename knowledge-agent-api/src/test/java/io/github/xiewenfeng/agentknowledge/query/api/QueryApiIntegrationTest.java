package io.github.xiewenfeng.agentknowledge.query.api;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.greaterThan;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class QueryApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void importsThenQueriesKnowledgeWithCitationsAndTrace() throws Exception {
        mockMvc.perform(post("/api/v1/documents/import")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "sourcePath": "docs/architecture.zh.md",
                                  "title": "Architecture",
                                  "content": "# Architecture\\n\\nIntro.\\n\\n## Commit Policy\\n\\nExplicit memory requests can usually be committed directly after confirmation.",
                                  "metadata": {
                                    "project": "agent-global-context",
                                    "sourceType": "markdown"
                                  }
                                }
                                """))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/v1/query")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "query": "memory commit",
                                  "topK": 5,
                                  "metadataFilter": {
                                    "project": "agent-global-context"
                                  }
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.answer").value(Matchers.containsString("Commit Policy")))
                .andExpect(jsonPath("$.citations.length()", greaterThan(0)))
                .andExpect(jsonPath("$.citations[0].sourcePath").value("docs/architecture.zh.md"))
                .andExpect(jsonPath("$.traceId").value(Matchers.startsWith("trace_")));
    }
}
