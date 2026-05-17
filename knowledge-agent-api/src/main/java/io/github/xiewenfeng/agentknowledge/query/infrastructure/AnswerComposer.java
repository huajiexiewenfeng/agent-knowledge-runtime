package io.github.xiewenfeng.agentknowledge.query.infrastructure;

import io.github.xiewenfeng.agentknowledge.query.domain.RetrievedChunk;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class AnswerComposer {

    public String compose(String query, List<RetrievedChunk> retrievedChunks) {
        if (retrievedChunks.isEmpty()) {
            return "No relevant knowledge chunks were found for query: " + query;
        }

        String evidence = retrievedChunks.stream()
                .map(hit -> "- [" + hit.chunk().getHeading() + "] " + firstSentence(hit.chunk().getContent()))
                .collect(Collectors.joining(System.lineSeparator()));

        return "Answer draft based on retrieved knowledge:" + System.lineSeparator() + evidence;
    }

    private String firstSentence(String content) {
        String trimmed = content.strip();
        int end = trimmed.indexOf('.');
        if (end > 0 && end < 240) {
            return trimmed.substring(0, end + 1);
        }
        return trimmed.length() <= 240 ? trimmed : trimmed.substring(0, 237) + "...";
    }
}
