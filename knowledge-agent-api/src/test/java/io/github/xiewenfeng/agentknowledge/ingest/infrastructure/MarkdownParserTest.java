package io.github.xiewenfeng.agentknowledge.ingest.infrastructure;

import io.github.xiewenfeng.agentknowledge.ingest.domain.ParsedMarkdownSection;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class MarkdownParserTest {

    @Test
    void parsesHeadingSections() {
        MarkdownParser parser = new MarkdownParser();
        String markdown = """
                # Architecture

                Intro paragraph.

                ## Commit Policy

                Explicit memory requests can usually be committed directly.

                ## Recall Policy

                Recall should load only relevant memory.
                """;

        List<ParsedMarkdownSection> sections = parser.parse(markdown);

        assertThat(sections).hasSize(3);
        assertThat(sections.get(0).heading()).isEqualTo("Architecture");
        assertThat(sections.get(0).content()).contains("Intro paragraph.");
        assertThat(sections.get(1).heading()).isEqualTo("Commit Policy");
        assertThat(sections.get(1).content()).contains("Explicit memory requests");
        assertThat(sections.get(2).heading()).isEqualTo("Recall Policy");
    }

    @Test
    void usesUntitledWhenContentHasNoHeading() {
        MarkdownParser parser = new MarkdownParser();

        List<ParsedMarkdownSection> sections = parser.parse("plain text only");

        assertThat(sections).hasSize(1);
        assertThat(sections.get(0).heading()).isEqualTo("Untitled");
        assertThat(sections.get(0).content()).isEqualTo("plain text only");
    }
}
