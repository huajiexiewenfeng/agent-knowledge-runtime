package io.github.xiewenfeng.agentknowledge.ingest.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.greaterThan;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ImportDocumentIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void importsMarkdownAndFetchesDocument() throws Exception {
        String body = """
                {
                  "sourcePath": "docs/architecture.zh.md",
                  "title": "Architecture",
                  "content": "# Architecture\\n\\nIntro.\\n\\n## Commit Policy\\n\\nExplicit memory requests can usually be committed directly.",
                  "metadata": {
                    "project": "agent-global-context",
                    "sourceType": "markdown"
                  }
                }
                """;

        String response = mockMvc.perform(post("/api/v1/documents/import")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.documentId").isNumber())
                .andExpect(jsonPath("$.chunkCount", greaterThan(0)))
                .andExpect(jsonPath("$.status").value("IMPORTED"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        String documentId = response.replaceAll(".*\\\"documentId\\\":(\\d+).*", "$1");

        mockMvc.perform(get("/api/v1/documents/" + documentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sourcePath").value("docs/architecture.zh.md"))
                .andExpect(jsonPath("$.title").value("Architecture"))
                .andExpect(jsonPath("$.chunkCount", greaterThan(0)));
    }
}
