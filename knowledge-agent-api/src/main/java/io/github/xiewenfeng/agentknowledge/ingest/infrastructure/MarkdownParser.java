package io.github.xiewenfeng.agentknowledge.ingest.infrastructure;

import io.github.xiewenfeng.agentknowledge.ingest.domain.ParsedMarkdownSection;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class MarkdownParser {

    public List<ParsedMarkdownSection> parse(String markdown) {
        if (markdown == null || markdown.isBlank()) {
            throw new IllegalArgumentException("Markdown content must not be blank");
        }

        List<ParsedMarkdownSection> sections = new ArrayList<>();
        String currentHeading = "Untitled";
        StringBuilder currentContent = new StringBuilder();
        boolean hasSeenHeading = false;

        for (String line : markdown.split("\\R")) {
            if (line.matches("^#{1,6}\\s+.+$")) {
                if (hasSeenHeading || currentContent.length() > 0) {
                    sections.add(new ParsedMarkdownSection(currentHeading, currentContent.toString().trim()));
                    currentContent.setLength(0);
                }
                currentHeading = line.replaceFirst("^#{1,6}\\s+", "").trim();
                hasSeenHeading = true;
            } else {
                currentContent.append(line).append(System.lineSeparator());
            }
        }

        if (hasSeenHeading || currentContent.length() > 0) {
            sections.add(new ParsedMarkdownSection(currentHeading, currentContent.toString().trim()));
        }

        return sections.stream()
                .filter(section -> !section.content().isBlank())
                .toList();
    }
}
