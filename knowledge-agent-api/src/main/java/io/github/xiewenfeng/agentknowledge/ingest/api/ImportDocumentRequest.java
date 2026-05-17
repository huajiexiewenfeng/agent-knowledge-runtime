package io.github.xiewenfeng.agentknowledge.ingest.api;

import jakarta.validation.constraints.NotBlank;

import java.util.Map;

public record ImportDocumentRequest(
        @NotBlank String sourcePath,
        @NotBlank String title,
        @NotBlank String content,
        Map<String, String> metadata
) {
}
