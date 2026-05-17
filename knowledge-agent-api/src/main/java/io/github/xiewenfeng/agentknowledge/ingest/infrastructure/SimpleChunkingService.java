package io.github.xiewenfeng.agentknowledge.ingest.infrastructure;

import io.github.xiewenfeng.agentknowledge.ingest.domain.DocumentChunk;
import io.github.xiewenfeng.agentknowledge.ingest.domain.ParsedMarkdownSection;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;

@Component
public class SimpleChunkingService {

    public List<DocumentChunk> createChunks(Long documentId, String sourcePath,
                                            List<ParsedMarkdownSection> sections,
                                            String metadataJson) {
        if (sections == null || sections.isEmpty()) {
            throw new IllegalArgumentException("Parsed sections must not be empty");
        }

        List<DocumentChunk> chunks = new ArrayList<>();
        Instant now = Instant.now();
        for (int index = 0; index < sections.size(); index++) {
            ParsedMarkdownSection section = sections.get(index);
            chunks.add(new DocumentChunk(
                    documentId,
                    sourcePath,
                    section.heading(),
                    index,
                    section.content(),
                    sha256(section.heading() + "\n" + section.content()),
                    metadataJson,
                    now
            ));
        }
        return chunks;
    }

    private String sha256(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(value.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is not available", exception);
        }
    }
}
