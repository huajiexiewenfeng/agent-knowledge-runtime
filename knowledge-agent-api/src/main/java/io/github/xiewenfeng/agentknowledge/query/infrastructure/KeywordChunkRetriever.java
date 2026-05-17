package io.github.xiewenfeng.agentknowledge.query.infrastructure;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.xiewenfeng.agentknowledge.ingest.domain.DocumentChunk;
import io.github.xiewenfeng.agentknowledge.ingest.infrastructure.ChunkRepository;
import io.github.xiewenfeng.agentknowledge.query.domain.RetrievedChunk;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class KeywordChunkRetriever {
    private final ChunkRepository chunkRepository;
    private final ObjectMapper objectMapper;

    public KeywordChunkRetriever(ChunkRepository chunkRepository, ObjectMapper objectMapper) {
        this.chunkRepository = chunkRepository;
        this.objectMapper = objectMapper;
    }

    public List<RetrievedChunk> retrieve(String query, int topK, Map<String, String> metadataFilter) {
        Set<String> terms = normalizeTerms(query);
        return chunkRepository.findAll().stream()
                .filter(chunk -> matchesMetadata(chunk, metadataFilter))
                .map(chunk -> new RetrievedChunk(chunk, score(chunk, terms)))
                .filter(hit -> hit.score() > 0)
                .sorted((left, right) -> Integer.compare(right.score(), left.score()))
                .limit(topK)
                .toList();
    }

    private int score(DocumentChunk chunk, Set<String> terms) {
        String searchable = (chunk.getHeading() + " " + chunk.getContent()).toLowerCase(Locale.ROOT);
        int score = 0;
        for (String term : terms) {
            if (searchable.contains(term)) {
                score++;
            }
        }
        return score;
    }

    private Set<String> normalizeTerms(String query) {
        return Arrays.stream(query.toLowerCase(Locale.ROOT).split("[^\\p{IsAlphabetic}\\p{IsDigit}\\p{IsHan}]+"))
                .map(String::trim)
                .filter(term -> term.length() >= 2)
                .collect(Collectors.toSet());
    }

    private boolean matchesMetadata(DocumentChunk chunk, Map<String, String> metadataFilter) {
        if (metadataFilter == null || metadataFilter.isEmpty()) {
            return true;
        }
        try {
            Map<String, String> metadata = objectMapper.readValue(chunk.getMetadataJson(), new TypeReference<>() {});
            return metadataFilter.entrySet().stream()
                    .allMatch(entry -> entry.getValue().equals(metadata.get(entry.getKey())));
        } catch (Exception exception) {
            return false;
        }
    }
}
