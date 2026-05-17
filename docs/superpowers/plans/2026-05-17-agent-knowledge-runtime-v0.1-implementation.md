# Agent Knowledge Runtime v0.1 Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Build the first runnable Java/Spring Boot RAG backend loop for Markdown import, chunking, keyword retrieval, citations, and trace lookup.

**Architecture:** Create one Maven-based Spring Boot module named `knowledge-agent-api`. Keep v0.1 as a single service with clear package boundaries: `ingest`, `query`, and `common`. Use H2 with Spring Data JPA for persistence and deterministic keyword retrieval instead of LLM/vector search.

**Tech Stack:** Java 21, Spring Boot 3.3.x, Maven, Spring Web, Spring Data JPA, H2, Jakarta Validation, JUnit 5, Spring Boot Test.

---

## Scope Check

The approved spec covers one coherent subsystem: the v0.1 RAG backend minimum loop. Memory, eval, real LLM calls, embeddings, vector databases, frontend UI, async indexing, and distributed queues are outside this plan.

## Target File Structure

Create and modify these files under `D:\workspace\ai-workspace\agent-knowledge-runtime`:

```text
.gitignore
README.md
docs/superpowers/plans/2026-05-17-agent-knowledge-runtime-v0.1-implementation.md
knowledge-agent-api/pom.xml
knowledge-agent-api/src/main/java/com/xwf/agentknowledge/AgentKnowledgeApplication.java
knowledge-agent-api/src/main/java/com/xwf/agentknowledge/common/error/ApiErrorResponse.java
knowledge-agent-api/src/main/java/com/xwf/agentknowledge/common/error/ErrorCode.java
knowledge-agent-api/src/main/java/com/xwf/agentknowledge/common/error/GlobalExceptionHandler.java
knowledge-agent-api/src/main/java/com/xwf/agentknowledge/common/error/ResourceNotFoundException.java
knowledge-agent-api/src/main/java/com/xwf/agentknowledge/common/model/MetadataFilter.java
knowledge-agent-api/src/main/java/com/xwf/agentknowledge/common/trace/TraceEntity.java
knowledge-agent-api/src/main/java/com/xwf/agentknowledge/common/trace/TraceEvent.java
knowledge-agent-api/src/main/java/com/xwf/agentknowledge/common/trace/TraceRepository.java
knowledge-agent-api/src/main/java/com/xwf/agentknowledge/common/trace/TraceResponse.java
knowledge-agent-api/src/main/java/com/xwf/agentknowledge/common/trace/TraceService.java
knowledge-agent-api/src/main/java/com/xwf/agentknowledge/common/trace/TraceController.java
knowledge-agent-api/src/main/java/com/xwf/agentknowledge/eval/package-info.java
knowledge-agent-api/src/main/java/com/xwf/agentknowledge/ingest/api/DocumentController.java
knowledge-agent-api/src/main/java/com/xwf/agentknowledge/ingest/api/DocumentResponse.java
knowledge-agent-api/src/main/java/com/xwf/agentknowledge/ingest/api/ImportDocumentRequest.java
knowledge-agent-api/src/main/java/com/xwf/agentknowledge/ingest/api/ImportDocumentResponse.java
knowledge-agent-api/src/main/java/com/xwf/agentknowledge/ingest/application/ImportDocumentService.java
knowledge-agent-api/src/main/java/com/xwf/agentknowledge/ingest/domain/DocumentChunk.java
knowledge-agent-api/src/main/java/com/xwf/agentknowledge/ingest/domain/DocumentStatus.java
knowledge-agent-api/src/main/java/com/xwf/agentknowledge/ingest/domain/KnowledgeDocument.java
knowledge-agent-api/src/main/java/com/xwf/agentknowledge/ingest/domain/ParsedMarkdownSection.java
knowledge-agent-api/src/main/java/com/xwf/agentknowledge/ingest/infrastructure/ChunkRepository.java
knowledge-agent-api/src/main/java/com/xwf/agentknowledge/ingest/infrastructure/DocumentRepository.java
knowledge-agent-api/src/main/java/com/xwf/agentknowledge/ingest/infrastructure/MarkdownParser.java
knowledge-agent-api/src/main/java/com/xwf/agentknowledge/ingest/infrastructure/SimpleChunkingService.java
knowledge-agent-api/src/main/java/com/xwf/agentknowledge/memory/package-info.java
knowledge-agent-api/src/main/java/com/xwf/agentknowledge/query/api/KnowledgeQueryController.java
knowledge-agent-api/src/main/java/com/xwf/agentknowledge/query/api/KnowledgeQueryRequest.java
knowledge-agent-api/src/main/java/com/xwf/agentknowledge/query/api/KnowledgeQueryResponse.java
knowledge-agent-api/src/main/java/com/xwf/agentknowledge/query/application/QueryKnowledgeService.java
knowledge-agent-api/src/main/java/com/xwf/agentknowledge/query/domain/Citation.java
knowledge-agent-api/src/main/java/com/xwf/agentknowledge/query/domain/RetrievedChunk.java
knowledge-agent-api/src/main/java/com/xwf/agentknowledge/query/infrastructure/AnswerComposer.java
knowledge-agent-api/src/main/java/com/xwf/agentknowledge/query/infrastructure/CitationBuilder.java
knowledge-agent-api/src/main/java/com/xwf/agentknowledge/query/infrastructure/KeywordChunkRetriever.java
knowledge-agent-api/src/main/resources/application.yml
knowledge-agent-api/src/test/java/com/xwf/agentknowledge/ingest/infrastructure/MarkdownParserTest.java
knowledge-agent-api/src/test/java/com/xwf/agentknowledge/ingest/infrastructure/SimpleChunkingServiceTest.java
knowledge-agent-api/src/test/java/com/xwf/agentknowledge/ingest/api/ImportDocumentIntegrationTest.java
knowledge-agent-api/src/test/java/com/xwf/agentknowledge/query/application/QueryKnowledgeServiceTest.java
knowledge-agent-api/src/test/java/com/xwf/agentknowledge/query/api/QueryApiIntegrationTest.java
knowledge-agent-api/src/test/java/com/xwf/agentknowledge/common/trace/TraceControllerIntegrationTest.java
```

---

### Task 1: Scaffold Spring Boot Module

**Files:**
- Create: `.gitignore`
- Create: `README.md`
- Create: `knowledge-agent-api/pom.xml`
- Create: `knowledge-agent-api/src/main/java/com/xwf/agentknowledge/AgentKnowledgeApplication.java`
- Create: `knowledge-agent-api/src/main/resources/application.yml`
- Create: `knowledge-agent-api/src/main/java/com/xwf/agentknowledge/memory/package-info.java`
- Create: `knowledge-agent-api/src/main/java/com/xwf/agentknowledge/eval/package-info.java`

- [ ] **Step 1: Create `.gitignore`**

```gitignore
.idea/
*.iml
target/
.mvn/wrapper/maven-wrapper.jar
*.log
```

- [ ] **Step 2: Create root `README.md`**

```markdown
# Agent Knowledge Runtime

Java/Spring Boot backend runtime for agent knowledge ingestion, retrieval, citation, and traceability.

## v0.1 Scope

The first version implements a small but real RAG backend loop:

- Import Markdown content.
- Parse headings and split chunks.
- Store document and chunk metadata.
- Query chunks with deterministic keyword retrieval.
- Return answer drafts with citations.
- Record query traces.

LLM calls, embeddings, vector databases, memory workflows, eval runners, and UI are intentionally deferred.

## Module

- `knowledge-agent-api`: Spring Boot backend API.
```

- [ ] **Step 3: Create `knowledge-agent-api/pom.xml`**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.3.5</version>
        <relativePath/>
    </parent>

    <groupId>com.xwf</groupId>
    <artifactId>knowledge-agent-api</artifactId>
    <version>0.1.0-SNAPSHOT</version>
    <name>knowledge-agent-api</name>
    <description>Agent Knowledge Runtime backend API</description>

    <properties>
        <java.version>21</java.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>
        <dependency>
            <groupId>com.h2database</groupId>
            <artifactId>h2</artifactId>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
```

- [ ] **Step 4: Create the Spring Boot application class**

```java
package com.xwf.agentknowledge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AgentKnowledgeApplication {

    public static void main(String[] args) {
        SpringApplication.run(AgentKnowledgeApplication.class, args);
    }
}
```

- [ ] **Step 5: Create `application.yml`**

```yaml
spring:
  application:
    name: knowledge-agent-api
  datasource:
    url: jdbc:h2:mem:agentknowledge;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE
    driver-class-name: org.h2.Driver
    username: sa
    password:
  h2:
    console:
      enabled: true
  jpa:
    hibernate:
      ddl-auto: create-drop
    open-in-view: false
    properties:
      hibernate:
        format_sql: true

server:
  port: 8080
```

- [ ] **Step 6: Create reserved package markers**

`knowledge-agent-api/src/main/java/com/xwf/agentknowledge/memory/package-info.java`:

```java
/**
 * Reserved package for v0.4 memory candidate, review, commit, and recall flows.
 */
package com.xwf.agentknowledge.memory;
```

`knowledge-agent-api/src/main/java/com/xwf/agentknowledge/eval/package-info.java`:

```java
/**
 * Reserved package for v0.5 golden cases, failure cases, and trace reports.
 */
package com.xwf.agentknowledge.eval;
```

- [ ] **Step 7: Run compile**

Run:

```powershell
mvn -f knowledge-agent-api/pom.xml test
```

Expected: build succeeds with `BUILD SUCCESS`.

- [ ] **Step 8: Commit scaffold**

```powershell
git add .gitignore README.md knowledge-agent-api
git commit -m "feat: scaffold knowledge agent api"
```

---

### Task 2: Add Domain Models and Common Errors

**Files:**
- Create: `knowledge-agent-api/src/main/java/com/xwf/agentknowledge/common/error/ApiErrorResponse.java`
- Create: `knowledge-agent-api/src/main/java/com/xwf/agentknowledge/common/error/ErrorCode.java`
- Create: `knowledge-agent-api/src/main/java/com/xwf/agentknowledge/common/error/GlobalExceptionHandler.java`
- Create: `knowledge-agent-api/src/main/java/com/xwf/agentknowledge/common/error/ResourceNotFoundException.java`
- Create: `knowledge-agent-api/src/main/java/com/xwf/agentknowledge/common/model/MetadataFilter.java`
- Create: `knowledge-agent-api/src/main/java/com/xwf/agentknowledge/ingest/domain/DocumentStatus.java`
- Create: `knowledge-agent-api/src/main/java/com/xwf/agentknowledge/ingest/domain/KnowledgeDocument.java`
- Create: `knowledge-agent-api/src/main/java/com/xwf/agentknowledge/ingest/domain/DocumentChunk.java`
- Create: `knowledge-agent-api/src/main/java/com/xwf/agentknowledge/ingest/domain/ParsedMarkdownSection.java`
- Create: `knowledge-agent-api/src/main/java/com/xwf/agentknowledge/query/domain/Citation.java`
- Create: `knowledge-agent-api/src/main/java/com/xwf/agentknowledge/query/domain/RetrievedChunk.java`

- [ ] **Step 1: Create common error types**

`ErrorCode.java`:

```java
package com.xwf.agentknowledge.common.error;

public enum ErrorCode {
    INVALID_REQUEST,
    DOCUMENT_NOT_FOUND,
    DOCUMENT_IMPORT_FAILED,
    QUERY_EMPTY,
    NO_CHUNKS_FOUND,
    TRACE_NOT_FOUND,
    INTERNAL_ERROR
}
```

`ApiErrorResponse.java`:

```java
package com.xwf.agentknowledge.common.error;

import java.time.Instant;

public record ApiErrorResponse(
        ErrorCode errorCode,
        String message,
        String traceId,
        Instant timestamp
) {
    public static ApiErrorResponse of(ErrorCode errorCode, String message, String traceId) {
        return new ApiErrorResponse(errorCode, message, traceId, Instant.now());
    }
}
```

`ResourceNotFoundException.java`:

```java
package com.xwf.agentknowledge.common.error;

public class ResourceNotFoundException extends RuntimeException {
    private final ErrorCode errorCode;

    public ResourceNotFoundException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public ErrorCode errorCode() {
        return errorCode;
    }
}
```

`GlobalExceptionHandler.java`:

```java
package com.xwf.agentknowledge.common.error;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNotFound(ResourceNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiErrorResponse.of(exception.errorCode(), exception.getMessage(), null));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(MethodArgumentNotValidException exception) {
        String message = exception.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(error -> error.getField() + " " + error.getDefaultMessage())
                .orElse("Invalid request");
        return ResponseEntity.badRequest()
                .body(ApiErrorResponse.of(ErrorCode.INVALID_REQUEST, message, null));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalArgument(IllegalArgumentException exception) {
        return ResponseEntity.badRequest()
                .body(ApiErrorResponse.of(ErrorCode.INVALID_REQUEST, exception.getMessage(), null));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleUnexpected(Exception exception, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiErrorResponse.of(ErrorCode.INTERNAL_ERROR, "Internal server error", null));
    }
}
```

- [ ] **Step 2: Create metadata filter model**

```java
package com.xwf.agentknowledge.common.model;

import java.util.Map;

public record MetadataFilter(Map<String, String> values) {
    public static MetadataFilter empty() {
        return new MetadataFilter(Map.of());
    }

    public boolean isEmpty() {
        return values == null || values.isEmpty();
    }

    public Map<String, String> safeValues() {
        return values == null ? Map.of() : values;
    }
}
```

- [ ] **Step 3: Create ingest domain records**

`DocumentStatus.java`:

```java
package com.xwf.agentknowledge.ingest.domain;

public enum DocumentStatus {
    IMPORTED
}
```

`KnowledgeDocument.java`:

```java
package com.xwf.agentknowledge.ingest.domain;

import java.time.Instant;

public record KnowledgeDocument(
        Long id,
        String sourcePath,
        String title,
        String contentHash,
        Instant importedAt,
        Instant updatedAt,
        DocumentStatus status
) {
}
```

`DocumentChunk.java`:

```java
package com.xwf.agentknowledge.ingest.domain;

import java.time.Instant;

public record DocumentChunk(
        Long id,
        Long documentId,
        String sourcePath,
        String heading,
        int chunkIndex,
        String content,
        String contentHash,
        String metadataJson,
        Instant createdAt
) {
}
```

`ParsedMarkdownSection.java`:

```java
package com.xwf.agentknowledge.ingest.domain;

public record ParsedMarkdownSection(String heading, String content) {
}
```

- [ ] **Step 4: Create query domain records**

`Citation.java`:

```java
package com.xwf.agentknowledge.query.domain;

public record Citation(
        Long chunkId,
        Long documentId,
        String sourcePath,
        String heading,
        int chunkIndex,
        String snippet
) {
}
```

`RetrievedChunk.java`:

```java
package com.xwf.agentknowledge.query.domain;

import com.xwf.agentknowledge.ingest.domain.DocumentChunk;

public record RetrievedChunk(DocumentChunk chunk, int score) {
}
```

- [ ] **Step 5: Run compile**

Run:

```powershell
mvn -f knowledge-agent-api/pom.xml test
```

Expected: `BUILD SUCCESS`.

- [ ] **Step 6: Commit common models**

```powershell
git add knowledge-agent-api/src/main/java/com/xwf/agentknowledge
git commit -m "feat: add core domain models"
```

---

### Task 3: Add JPA Persistence

**Files:**
- Create: `knowledge-agent-api/src/main/java/com/xwf/agentknowledge/ingest/infrastructure/DocumentRepository.java`
- Create: `knowledge-agent-api/src/main/java/com/xwf/agentknowledge/ingest/infrastructure/ChunkRepository.java`
- Modify: `KnowledgeDocument.java`
- Modify: `DocumentChunk.java`

- [ ] **Step 1: Replace `KnowledgeDocument` with a JPA entity**

```java
package com.xwf.agentknowledge.ingest.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "knowledge_document")
public class KnowledgeDocument {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String sourcePath;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 64)
    private String contentHash;

    @Column(nullable = false)
    private Instant importedAt;

    @Column(nullable = false)
    private Instant updatedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DocumentStatus status;

    protected KnowledgeDocument() {
    }

    public KnowledgeDocument(String sourcePath, String title, String contentHash, Instant now) {
        this.sourcePath = sourcePath;
        this.title = title;
        this.contentHash = contentHash;
        this.importedAt = now;
        this.updatedAt = now;
        this.status = DocumentStatus.IMPORTED;
    }

    public Long getId() {
        return id;
    }

    public String getSourcePath() {
        return sourcePath;
    }

    public String getTitle() {
        return title;
    }

    public String getContentHash() {
        return contentHash;
    }

    public Instant getImportedAt() {
        return importedAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public DocumentStatus getStatus() {
        return status;
    }
}
```

- [ ] **Step 2: Replace `DocumentChunk` with a JPA entity**

```java
package com.xwf.agentknowledge.ingest.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "document_chunk")
public class DocumentChunk {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long documentId;

    @Column(nullable = false)
    private String sourcePath;

    @Column(nullable = false)
    private String heading;

    @Column(nullable = false)
    private int chunkIndex;

    @Column(nullable = false, length = 8000)
    private String content;

    @Column(nullable = false, length = 64)
    private String contentHash;

    @Column(nullable = false, length = 2000)
    private String metadataJson;

    @Column(nullable = false)
    private Instant createdAt;

    protected DocumentChunk() {
    }

    public DocumentChunk(Long documentId, String sourcePath, String heading, int chunkIndex,
                         String content, String contentHash, String metadataJson, Instant createdAt) {
        this.documentId = documentId;
        this.sourcePath = sourcePath;
        this.heading = heading;
        this.chunkIndex = chunkIndex;
        this.content = content;
        this.contentHash = contentHash;
        this.metadataJson = metadataJson;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public Long getDocumentId() {
        return documentId;
    }

    public String getSourcePath() {
        return sourcePath;
    }

    public String getHeading() {
        return heading;
    }

    public int getChunkIndex() {
        return chunkIndex;
    }

    public String getContent() {
        return content;
    }

    public String getContentHash() {
        return contentHash;
    }

    public String getMetadataJson() {
        return metadataJson;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
```

- [ ] **Step 3: Create repositories**

`DocumentRepository.java`:

```java
package com.xwf.agentknowledge.ingest.infrastructure;

import com.xwf.agentknowledge.ingest.domain.KnowledgeDocument;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentRepository extends JpaRepository<KnowledgeDocument, Long> {
}
```

`ChunkRepository.java`:

```java
package com.xwf.agentknowledge.ingest.infrastructure;

import com.xwf.agentknowledge.ingest.domain.DocumentChunk;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChunkRepository extends JpaRepository<DocumentChunk, Long> {
    List<DocumentChunk> findByDocumentIdOrderByChunkIndexAsc(Long documentId);
}
```

- [ ] **Step 4: Run compile**

Run:

```powershell
mvn -f knowledge-agent-api/pom.xml test
```

Expected: `BUILD SUCCESS`.

- [ ] **Step 5: Commit persistence**

```powershell
git add knowledge-agent-api/src/main/java/com/xwf/agentknowledge/ingest
git commit -m "feat: add document chunk persistence"
```

---

### Task 4: Implement Markdown Parsing and Chunking

**Files:**
- Create: `knowledge-agent-api/src/main/java/com/xwf/agentknowledge/ingest/infrastructure/MarkdownParser.java`
- Create: `knowledge-agent-api/src/main/java/com/xwf/agentknowledge/ingest/infrastructure/SimpleChunkingService.java`
- Create: `knowledge-agent-api/src/test/java/com/xwf/agentknowledge/ingest/infrastructure/MarkdownParserTest.java`
- Create: `knowledge-agent-api/src/test/java/com/xwf/agentknowledge/ingest/infrastructure/SimpleChunkingServiceTest.java`

- [ ] **Step 1: Write failing parser test**

```java
package com.xwf.agentknowledge.ingest.infrastructure;

import com.xwf.agentknowledge.ingest.domain.ParsedMarkdownSection;
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
```

- [ ] **Step 2: Run parser test and verify it fails**

Run:

```powershell
mvn -f knowledge-agent-api/pom.xml -Dtest=MarkdownParserTest test
```

Expected: FAIL because `MarkdownParser` does not exist.

- [ ] **Step 3: Implement `MarkdownParser`**

```java
package com.xwf.agentknowledge.ingest.infrastructure;

import com.xwf.agentknowledge.ingest.domain.ParsedMarkdownSection;
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
                if (hasSeenHeading || !currentContent.isEmpty()) {
                    sections.add(new ParsedMarkdownSection(currentHeading, currentContent.toString().trim()));
                    currentContent.setLength(0);
                }
                currentHeading = line.replaceFirst("^#{1,6}\\s+", "").trim();
                hasSeenHeading = true;
            } else {
                currentContent.append(line).append(System.lineSeparator());
            }
        }

        if (hasSeenHeading || !currentContent.isEmpty()) {
            sections.add(new ParsedMarkdownSection(currentHeading, currentContent.toString().trim()));
        }

        return sections.stream()
                .filter(section -> !section.content().isBlank())
                .toList();
    }
}
```

- [ ] **Step 4: Run parser test and verify it passes**

Run:

```powershell
mvn -f knowledge-agent-api/pom.xml -Dtest=MarkdownParserTest test
```

Expected: PASS.

- [ ] **Step 5: Write failing chunking test**

```java
package com.xwf.agentknowledge.ingest.infrastructure;

import com.xwf.agentknowledge.ingest.domain.DocumentChunk;
import com.xwf.agentknowledge.ingest.domain.ParsedMarkdownSection;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SimpleChunkingServiceTest {

    @Test
    void createsChunksWithSourceHeadingAndIndex() {
        SimpleChunkingService service = new SimpleChunkingService();
        List<ParsedMarkdownSection> sections = List.of(
                new ParsedMarkdownSection("Commit Policy", "Explicit memory requests can usually be committed directly."),
                new ParsedMarkdownSection("Recall Policy", "Recall should load only relevant memory.")
        );

        List<DocumentChunk> chunks = service.createChunks(
                10L,
                "docs/architecture.zh.md",
                sections,
                "{\"project\":\"agent-global-context\"}"
        );

        assertThat(chunks).hasSize(2);
        assertThat(chunks.get(0).getDocumentId()).isEqualTo(10L);
        assertThat(chunks.get(0).getSourcePath()).isEqualTo("docs/architecture.zh.md");
        assertThat(chunks.get(0).getHeading()).isEqualTo("Commit Policy");
        assertThat(chunks.get(0).getChunkIndex()).isZero();
        assertThat(chunks.get(0).getContentHash()).hasSize(64);
        assertThat(chunks.get(0).getMetadataJson()).contains("agent-global-context");
    }
}
```

- [ ] **Step 6: Run chunking test and verify it fails**

Run:

```powershell
mvn -f knowledge-agent-api/pom.xml -Dtest=SimpleChunkingServiceTest test
```

Expected: FAIL because `SimpleChunkingService` does not exist.

- [ ] **Step 7: Implement `SimpleChunkingService`**

```java
package com.xwf.agentknowledge.ingest.infrastructure;

import com.xwf.agentknowledge.ingest.domain.DocumentChunk;
import com.xwf.agentknowledge.ingest.domain.ParsedMarkdownSection;
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
```

- [ ] **Step 8: Run parser and chunking tests**

Run:

```powershell
mvn -f knowledge-agent-api/pom.xml -Dtest=MarkdownParserTest,SimpleChunkingServiceTest test
```

Expected: PASS.

- [ ] **Step 9: Commit parser and chunking**

```powershell
git add knowledge-agent-api/src/main/java/com/xwf/agentknowledge/ingest knowledge-agent-api/src/test/java/com/xwf/agentknowledge/ingest
git commit -m "feat: parse markdown into chunks"
```

---

### Task 5: Implement Document Import API

**Files:**
- Create: `knowledge-agent-api/src/main/java/com/xwf/agentknowledge/ingest/api/DocumentController.java`
- Create: `knowledge-agent-api/src/main/java/com/xwf/agentknowledge/ingest/api/DocumentResponse.java`
- Create: `knowledge-agent-api/src/main/java/com/xwf/agentknowledge/ingest/api/ImportDocumentRequest.java`
- Create: `knowledge-agent-api/src/main/java/com/xwf/agentknowledge/ingest/api/ImportDocumentResponse.java`
- Create: `knowledge-agent-api/src/main/java/com/xwf/agentknowledge/ingest/application/ImportDocumentService.java`
- Create: `knowledge-agent-api/src/test/java/com/xwf/agentknowledge/ingest/api/ImportDocumentIntegrationTest.java`

- [ ] **Step 1: Write failing import integration test**

```java
package com.xwf.agentknowledge.ingest.api;

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
```

- [ ] **Step 2: Run import test and verify it fails**

Run:

```powershell
mvn -f knowledge-agent-api/pom.xml -Dtest=ImportDocumentIntegrationTest test
```

Expected: FAIL because import API classes do not exist.

- [ ] **Step 3: Create API DTOs**

`ImportDocumentRequest.java`:

```java
package com.xwf.agentknowledge.ingest.api;

import jakarta.validation.constraints.NotBlank;

import java.util.Map;

public record ImportDocumentRequest(
        @NotBlank String sourcePath,
        @NotBlank String title,
        @NotBlank String content,
        Map<String, String> metadata
) {
}
```

`ImportDocumentResponse.java`:

```java
package com.xwf.agentknowledge.ingest.api;

public record ImportDocumentResponse(Long documentId, int chunkCount, String status) {
}
```

`DocumentResponse.java`:

```java
package com.xwf.agentknowledge.ingest.api;

import com.xwf.agentknowledge.ingest.domain.KnowledgeDocument;

import java.time.Instant;

public record DocumentResponse(
        Long documentId,
        String sourcePath,
        String title,
        String status,
        int chunkCount,
        Instant importedAt,
        Instant updatedAt
) {
    public static DocumentResponse from(KnowledgeDocument document, int chunkCount) {
        return new DocumentResponse(
                document.getId(),
                document.getSourcePath(),
                document.getTitle(),
                document.getStatus().name(),
                chunkCount,
                document.getImportedAt(),
                document.getUpdatedAt()
        );
    }
}
```

- [ ] **Step 4: Create `ImportDocumentService`**

```java
package com.xwf.agentknowledge.ingest.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xwf.agentknowledge.common.error.ErrorCode;
import com.xwf.agentknowledge.common.error.ResourceNotFoundException;
import com.xwf.agentknowledge.ingest.api.DocumentResponse;
import com.xwf.agentknowledge.ingest.api.ImportDocumentRequest;
import com.xwf.agentknowledge.ingest.api.ImportDocumentResponse;
import com.xwf.agentknowledge.ingest.domain.DocumentChunk;
import com.xwf.agentknowledge.ingest.domain.KnowledgeDocument;
import com.xwf.agentknowledge.ingest.domain.ParsedMarkdownSection;
import com.xwf.agentknowledge.ingest.infrastructure.ChunkRepository;
import com.xwf.agentknowledge.ingest.infrastructure.DocumentRepository;
import com.xwf.agentknowledge.ingest.infrastructure.MarkdownParser;
import com.xwf.agentknowledge.ingest.infrastructure.SimpleChunkingService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;

@Service
public class ImportDocumentService {
    private final DocumentRepository documentRepository;
    private final ChunkRepository chunkRepository;
    private final MarkdownParser markdownParser;
    private final SimpleChunkingService chunkingService;
    private final ObjectMapper objectMapper;

    public ImportDocumentService(DocumentRepository documentRepository,
                                 ChunkRepository chunkRepository,
                                 MarkdownParser markdownParser,
                                 SimpleChunkingService chunkingService,
                                 ObjectMapper objectMapper) {
        this.documentRepository = documentRepository;
        this.chunkRepository = chunkRepository;
        this.markdownParser = markdownParser;
        this.chunkingService = chunkingService;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public ImportDocumentResponse importDocument(ImportDocumentRequest request) {
        Instant now = Instant.now();
        KnowledgeDocument document = documentRepository.save(new KnowledgeDocument(
                request.sourcePath(),
                request.title(),
                sha256(request.content()),
                now
        ));

        List<ParsedMarkdownSection> sections = markdownParser.parse(request.content());
        List<DocumentChunk> chunks = chunkingService.createChunks(
                document.getId(),
                document.getSourcePath(),
                sections,
                toJson(request.metadata())
        );
        chunkRepository.saveAll(chunks);

        return new ImportDocumentResponse(document.getId(), chunks.size(), document.getStatus().name());
    }

    @Transactional(readOnly = true)
    public DocumentResponse getDocument(Long documentId) {
        KnowledgeDocument document = documentRepository.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.DOCUMENT_NOT_FOUND, "Document not found: " + documentId));
        int chunkCount = chunkRepository.findByDocumentIdOrderByChunkIndexAsc(documentId).size();
        return DocumentResponse.from(document, chunkCount);
    }

    private String toJson(Map<String, String> metadata) {
        try {
            return objectMapper.writeValueAsString(metadata == null ? Map.of() : metadata);
        } catch (JsonProcessingException exception) {
            throw new IllegalArgumentException("Metadata must be valid JSON object", exception);
        }
    }

    private String sha256(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(value.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is not available", exception);
        }
    }
}
```

- [ ] **Step 5: Create `DocumentController`**

```java
package com.xwf.agentknowledge.ingest.api;

import com.xwf.agentknowledge.ingest.application.ImportDocumentService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/documents")
public class DocumentController {
    private final ImportDocumentService importDocumentService;

    public DocumentController(ImportDocumentService importDocumentService) {
        this.importDocumentService = importDocumentService;
    }

    @PostMapping("/import")
    public ImportDocumentResponse importDocument(@Valid @RequestBody ImportDocumentRequest request) {
        return importDocumentService.importDocument(request);
    }

    @GetMapping("/{documentId}")
    public DocumentResponse getDocument(@PathVariable Long documentId) {
        return importDocumentService.getDocument(documentId);
    }
}
```

- [ ] **Step 6: Run import integration test**

Run:

```powershell
mvn -f knowledge-agent-api/pom.xml -Dtest=ImportDocumentIntegrationTest test
```

Expected: PASS.

- [ ] **Step 7: Commit import API**

```powershell
git add knowledge-agent-api/src/main/java/com/xwf/agentknowledge/ingest knowledge-agent-api/src/test/java/com/xwf/agentknowledge/ingest
git commit -m "feat: add markdown import api"
```

---

### Task 6: Implement Query, Citation, and Answer Draft

**Files:**
- Create: `knowledge-agent-api/src/main/java/com/xwf/agentknowledge/query/api/KnowledgeQueryController.java`
- Create: `knowledge-agent-api/src/main/java/com/xwf/agentknowledge/query/api/KnowledgeQueryRequest.java`
- Create: `knowledge-agent-api/src/main/java/com/xwf/agentknowledge/query/api/KnowledgeQueryResponse.java`
- Create: `knowledge-agent-api/src/main/java/com/xwf/agentknowledge/query/application/QueryKnowledgeService.java`
- Create: `knowledge-agent-api/src/main/java/com/xwf/agentknowledge/query/infrastructure/AnswerComposer.java`
- Create: `knowledge-agent-api/src/main/java/com/xwf/agentknowledge/query/infrastructure/CitationBuilder.java`
- Create: `knowledge-agent-api/src/main/java/com/xwf/agentknowledge/query/infrastructure/KeywordChunkRetriever.java`
- Create: `knowledge-agent-api/src/test/java/com/xwf/agentknowledge/query/application/QueryKnowledgeServiceTest.java`

- [ ] **Step 1: Write failing query service test**

```java
package com.xwf.agentknowledge.query.application;

import com.xwf.agentknowledge.ingest.domain.DocumentChunk;
import com.xwf.agentknowledge.ingest.infrastructure.ChunkRepository;
import com.xwf.agentknowledge.query.api.KnowledgeQueryRequest;
import com.xwf.agentknowledge.query.api.KnowledgeQueryResponse;
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
        assertThat(response.citations()).hasSize(1);
        assertThat(response.citations().get(0).sourcePath()).isEqualTo("docs/architecture.zh.md");
        assertThat(response.citations().get(0).heading()).isEqualTo("Commit Policy");
    }
}
```

- [ ] **Step 2: Run query service test and verify it fails**

Run:

```powershell
mvn -f knowledge-agent-api/pom.xml -Dtest=QueryKnowledgeServiceTest test
```

Expected: FAIL because query classes do not exist.

- [ ] **Step 3: Create query API DTOs**

`KnowledgeQueryRequest.java`:

```java
package com.xwf.agentknowledge.query.api;

import jakarta.validation.constraints.NotBlank;

import java.util.Map;

public record KnowledgeQueryRequest(
        @NotBlank String query,
        Integer topK,
        Map<String, String> metadataFilter
) {
    public int effectiveTopK() {
        if (topK == null || topK < 1) {
            return 5;
        }
        return Math.min(topK, 20);
    }
}
```

`KnowledgeQueryResponse.java`:

```java
package com.xwf.agentknowledge.query.api;

import com.xwf.agentknowledge.query.domain.Citation;

import java.util.List;

public record KnowledgeQueryResponse(
        String answer,
        List<Citation> citations,
        String traceId
) {
}
```

- [ ] **Step 4: Create retrieval and composition infrastructure**

`KeywordChunkRetriever.java`:

```java
package com.xwf.agentknowledge.query.infrastructure;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xwf.agentknowledge.ingest.domain.DocumentChunk;
import com.xwf.agentknowledge.ingest.infrastructure.ChunkRepository;
import com.xwf.agentknowledge.query.domain.RetrievedChunk;
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
```

`CitationBuilder.java`:

```java
package com.xwf.agentknowledge.query.infrastructure;

import com.xwf.agentknowledge.ingest.domain.DocumentChunk;
import com.xwf.agentknowledge.query.domain.Citation;
import org.springframework.stereotype.Component;

@Component
public class CitationBuilder {

    public Citation from(DocumentChunk chunk) {
        return new Citation(
                chunk.getId(),
                chunk.getDocumentId(),
                chunk.getSourcePath(),
                chunk.getHeading(),
                chunk.getChunkIndex(),
                snippet(chunk.getContent())
        );
    }

    private String snippet(String content) {
        if (content.length() <= 180) {
            return content;
        }
        return content.substring(0, 177) + "...";
    }
}
```

`AnswerComposer.java`:

```java
package com.xwf.agentknowledge.query.infrastructure;

import com.xwf.agentknowledge.query.domain.RetrievedChunk;
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
```

- [ ] **Step 5: Create `QueryKnowledgeService`**

```java
package com.xwf.agentknowledge.query.application;

import com.xwf.agentknowledge.common.error.ErrorCode;
import com.xwf.agentknowledge.query.api.KnowledgeQueryRequest;
import com.xwf.agentknowledge.query.api.KnowledgeQueryResponse;
import com.xwf.agentknowledge.query.domain.Citation;
import com.xwf.agentknowledge.query.domain.RetrievedChunk;
import com.xwf.agentknowledge.query.infrastructure.AnswerComposer;
import com.xwf.agentknowledge.query.infrastructure.CitationBuilder;
import com.xwf.agentknowledge.query.infrastructure.KeywordChunkRetriever;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class QueryKnowledgeService {
    private final KeywordChunkRetriever retriever;
    private final CitationBuilder citationBuilder;
    private final AnswerComposer answerComposer;

    public QueryKnowledgeService(KeywordChunkRetriever retriever,
                                 CitationBuilder citationBuilder,
                                 AnswerComposer answerComposer) {
        this.retriever = retriever;
        this.citationBuilder = citationBuilder;
        this.answerComposer = answerComposer;
    }

    @Transactional(readOnly = true)
    public KnowledgeQueryResponse query(KnowledgeQueryRequest request) {
        if (request.query() == null || request.query().isBlank()) {
            throw new IllegalArgumentException(ErrorCode.QUERY_EMPTY.name());
        }

        List<RetrievedChunk> hits = retriever.retrieve(
                request.query(),
                request.effectiveTopK(),
                request.metadataFilter()
        );
        List<Citation> citations = hits.stream()
                .map(hit -> citationBuilder.from(hit.chunk()))
                .toList();
        return new KnowledgeQueryResponse(answerComposer.compose(request.query(), hits), citations, null);
    }
}
```

- [ ] **Step 6: Run query service test**

Run:

```powershell
mvn -f knowledge-agent-api/pom.xml -Dtest=QueryKnowledgeServiceTest test
```

Expected: PASS.

- [ ] **Step 7: Create query controller**

```java
package com.xwf.agentknowledge.query.api;

import com.xwf.agentknowledge.query.application.QueryKnowledgeService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/query")
public class KnowledgeQueryController {
    private final QueryKnowledgeService queryKnowledgeService;

    public KnowledgeQueryController(QueryKnowledgeService queryKnowledgeService) {
        this.queryKnowledgeService = queryKnowledgeService;
    }

    @PostMapping
    public KnowledgeQueryResponse query(@Valid @RequestBody KnowledgeQueryRequest request) {
        return queryKnowledgeService.query(request);
    }
}
```

- [ ] **Step 8: Commit query support**

```powershell
git add knowledge-agent-api/src/main/java/com/xwf/agentknowledge/query knowledge-agent-api/src/test/java/com/xwf/agentknowledge/query
git commit -m "feat: add keyword query and citations"
```

---

### Task 7: Add Trace Recording and Trace API

**Files:**
- Create: `knowledge-agent-api/src/main/java/com/xwf/agentknowledge/common/trace/TraceEntity.java`
- Create: `knowledge-agent-api/src/main/java/com/xwf/agentknowledge/common/trace/TraceEvent.java`
- Create: `knowledge-agent-api/src/main/java/com/xwf/agentknowledge/common/trace/TraceRepository.java`
- Create: `knowledge-agent-api/src/main/java/com/xwf/agentknowledge/common/trace/TraceResponse.java`
- Create: `knowledge-agent-api/src/main/java/com/xwf/agentknowledge/common/trace/TraceService.java`
- Create: `knowledge-agent-api/src/main/java/com/xwf/agentknowledge/common/trace/TraceController.java`
- Modify: `knowledge-agent-api/src/main/java/com/xwf/agentknowledge/query/application/QueryKnowledgeService.java`
- Create: `knowledge-agent-api/src/test/java/com/xwf/agentknowledge/common/trace/TraceControllerIntegrationTest.java`

- [ ] **Step 1: Write failing trace integration test**

```java
package com.xwf.agentknowledge.common.trace;

import com.xwf.agentknowledge.ingest.domain.DocumentChunk;
import com.xwf.agentknowledge.ingest.infrastructure.ChunkRepository;
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
```

- [ ] **Step 2: Run trace test and verify it fails**

Run:

```powershell
mvn -f knowledge-agent-api/pom.xml -Dtest=TraceControllerIntegrationTest test
```

Expected: FAIL because trace classes and API do not exist.

- [ ] **Step 3: Create trace persistence and API types**

`TraceEvent.java`:

```java
package com.xwf.agentknowledge.common.trace;

public record TraceEvent(String name, String detail) {
}
```

`TraceEntity.java`:

```java
package com.xwf.agentknowledge.common.trace;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "query_trace")
public class TraceEntity {
    @Id
    private String traceId;

    @Column(nullable = false, length = 2000)
    private String queryText;

    @Column(nullable = false, length = 2000)
    private String retrievedChunkIds;

    @Column(nullable = false, length = 4000)
    private String eventsJson;

    @Column(nullable = false)
    private long latencyMs;

    @Column(nullable = false)
    private Instant createdAt;

    protected TraceEntity() {
    }

    public TraceEntity(String traceId, String queryText, String retrievedChunkIds,
                       String eventsJson, long latencyMs, Instant createdAt) {
        this.traceId = traceId;
        this.queryText = queryText;
        this.retrievedChunkIds = retrievedChunkIds;
        this.eventsJson = eventsJson;
        this.latencyMs = latencyMs;
        this.createdAt = createdAt;
    }

    public String getTraceId() {
        return traceId;
    }

    public String getQueryText() {
        return queryText;
    }

    public String getRetrievedChunkIds() {
        return retrievedChunkIds;
    }

    public String getEventsJson() {
        return eventsJson;
    }

    public long getLatencyMs() {
        return latencyMs;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
```

`TraceRepository.java`:

```java
package com.xwf.agentknowledge.common.trace;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TraceRepository extends JpaRepository<TraceEntity, String> {
}
```

`TraceResponse.java`:

```java
package com.xwf.agentknowledge.common.trace;

import java.time.Instant;
import java.util.List;

public record TraceResponse(
        String traceId,
        String query,
        List<Long> retrievedChunkIds,
        List<TraceEvent> events,
        long latencyMs,
        Instant createdAt
) {
}
```

- [ ] **Step 4: Create `TraceService` and controller**

`TraceService.java`:

```java
package com.xwf.agentknowledge.common.trace;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xwf.agentknowledge.common.error.ErrorCode;
import com.xwf.agentknowledge.common.error.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class TraceService {
    private final TraceRepository traceRepository;
    private final ObjectMapper objectMapper;

    public TraceService(TraceRepository traceRepository, ObjectMapper objectMapper) {
        this.traceRepository = traceRepository;
        this.objectMapper = objectMapper;
    }

    public String record(String query, List<Long> retrievedChunkIds, List<TraceEvent> events, long latencyMs) {
        String traceId = "trace_" + UUID.randomUUID();
        traceRepository.save(new TraceEntity(
                traceId,
                query,
                toJson(retrievedChunkIds),
                toJson(events),
                latencyMs,
                Instant.now()
        ));
        return traceId;
    }

    public TraceResponse getTrace(String traceId) {
        TraceEntity entity = traceRepository.findById(traceId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.TRACE_NOT_FOUND, "Trace not found: " + traceId));
        return new TraceResponse(
                entity.getTraceId(),
                entity.getQueryText(),
                fromJson(entity.getRetrievedChunkIds(), new TypeReference<List<Long>>() {}),
                fromJson(entity.getEventsJson(), new TypeReference<List<TraceEvent>>() {}),
                entity.getLatencyMs(),
                entity.getCreatedAt()
        );
    }

    private String toJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Unable to serialize trace value", exception);
        }
    }

    private <T> T fromJson(String json, TypeReference<T> typeReference) {
        try {
            return objectMapper.readValue(json, typeReference);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Unable to deserialize trace value", exception);
        }
    }
}
```

`TraceController.java`:

```java
package com.xwf.agentknowledge.common.trace;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/traces")
public class TraceController {
    private final TraceService traceService;

    public TraceController(TraceService traceService) {
        this.traceService = traceService;
    }

    @GetMapping("/{traceId}")
    public TraceResponse getTrace(@PathVariable String traceId) {
        return traceService.getTrace(traceId);
    }
}
```

- [ ] **Step 5: Update `QueryKnowledgeService` to record trace**

```java
package com.xwf.agentknowledge.query.application;

import com.xwf.agentknowledge.common.error.ErrorCode;
import com.xwf.agentknowledge.common.trace.TraceEvent;
import com.xwf.agentknowledge.common.trace.TraceService;
import com.xwf.agentknowledge.query.api.KnowledgeQueryRequest;
import com.xwf.agentknowledge.query.api.KnowledgeQueryResponse;
import com.xwf.agentknowledge.query.domain.Citation;
import com.xwf.agentknowledge.query.domain.RetrievedChunk;
import com.xwf.agentknowledge.query.infrastructure.AnswerComposer;
import com.xwf.agentknowledge.query.infrastructure.CitationBuilder;
import com.xwf.agentknowledge.query.infrastructure.KeywordChunkRetriever;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class QueryKnowledgeService {
    private final KeywordChunkRetriever retriever;
    private final CitationBuilder citationBuilder;
    private final AnswerComposer answerComposer;
    private final TraceService traceService;

    public QueryKnowledgeService(KeywordChunkRetriever retriever,
                                 CitationBuilder citationBuilder,
                                 AnswerComposer answerComposer,
                                 TraceService traceService) {
        this.retriever = retriever;
        this.citationBuilder = citationBuilder;
        this.answerComposer = answerComposer;
        this.traceService = traceService;
    }

    @Transactional
    public KnowledgeQueryResponse query(KnowledgeQueryRequest request) {
        if (request.query() == null || request.query().isBlank()) {
            throw new IllegalArgumentException(ErrorCode.QUERY_EMPTY.name());
        }

        long startedAt = System.nanoTime();
        List<RetrievedChunk> hits = retriever.retrieve(
                request.query(),
                request.effectiveTopK(),
                request.metadataFilter()
        );
        List<Citation> citations = hits.stream()
                .map(hit -> citationBuilder.from(hit.chunk()))
                .toList();
        String answer = answerComposer.compose(request.query(), hits);
        long latencyMs = (System.nanoTime() - startedAt) / 1_000_000;

        List<Long> retrievedChunkIds = hits.stream()
                .map(hit -> hit.chunk().getId())
                .toList();
        String traceId = traceService.record(
                request.query(),
                retrievedChunkIds,
                List.of(
                        new TraceEvent("query.received", "topK=" + request.effectiveTopK()),
                        new TraceEvent("chunks.retrieved", "count=" + hits.size())
                ),
                latencyMs
        );

        return new KnowledgeQueryResponse(answer, citations, traceId);
    }
}
```

- [ ] **Step 6: Run trace test**

Run:

```powershell
mvn -f knowledge-agent-api/pom.xml -Dtest=TraceControllerIntegrationTest test
```

Expected: PASS.

- [ ] **Step 7: Run query service test again**

Run:

```powershell
mvn -f knowledge-agent-api/pom.xml -Dtest=QueryKnowledgeServiceTest test
```

Expected: PASS after adjusting the assertion to require a non-blank trace id:

```java
assertThat(response.traceId()).startsWith("trace_");
```

- [ ] **Step 8: Commit trace support**

```powershell
git add knowledge-agent-api/src/main/java/com/xwf/agentknowledge/common/trace knowledge-agent-api/src/main/java/com/xwf/agentknowledge/query/application/QueryKnowledgeService.java knowledge-agent-api/src/test/java/com/xwf/agentknowledge
git commit -m "feat: record query traces"
```

---

### Task 8: Add End-to-End Query API Test and Documentation

**Files:**
- Create: `knowledge-agent-api/src/test/java/com/xwf/agentknowledge/query/api/QueryApiIntegrationTest.java`
- Modify: `README.md`

- [ ] **Step 1: Write end-to-end API test**

```java
package com.xwf.agentknowledge.query.api;

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
                .andExpect(jsonPath("$.answer").value(org.hamcrest.Matchers.containsString("Commit Policy")))
                .andExpect(jsonPath("$.citations.length()", greaterThan(0)))
                .andExpect(jsonPath("$.citations[0].sourcePath").value("docs/architecture.zh.md"))
                .andExpect(jsonPath("$.traceId").value(org.hamcrest.Matchers.startsWith("trace_")));
    }
}
```

- [ ] **Step 2: Run end-to-end API test**

Run:

```powershell
mvn -f knowledge-agent-api/pom.xml -Dtest=QueryApiIntegrationTest test
```

Expected: PASS.

- [ ] **Step 3: Update `README.md` with run and API examples**

```markdown
# Agent Knowledge Runtime

Java/Spring Boot backend runtime for agent knowledge ingestion, retrieval, citation, and traceability.

## v0.1 Scope

The first version implements a small but real RAG backend loop:

- Import Markdown content.
- Parse headings and split chunks.
- Store document and chunk metadata.
- Query chunks with deterministic keyword retrieval.
- Return answer drafts with citations.
- Record query traces.

LLM calls, embeddings, vector databases, memory workflows, eval runners, and UI are intentionally deferred.

## Module

- `knowledge-agent-api`: Spring Boot backend API.

## Run Tests

```powershell
mvn -f knowledge-agent-api/pom.xml test
```

## Run Locally

```powershell
mvn -f knowledge-agent-api/pom.xml spring-boot:run
```

The service starts on `http://localhost:8080`.

## Import Markdown

```powershell
curl -X POST http://localhost:8080/api/v1/documents/import `
  -H "Content-Type: application/json" `
  -d "{\"sourcePath\":\"docs/architecture.zh.md\",\"title\":\"Architecture\",\"content\":\"# Architecture\n\n## Commit Policy\n\nExplicit memory requests can usually be committed directly.\",\"metadata\":{\"project\":\"agent-global-context\",\"sourceType\":\"markdown\"}}"
```

## Query Knowledge

```powershell
curl -X POST http://localhost:8080/api/v1/query `
  -H "Content-Type: application/json" `
  -d "{\"query\":\"memory commit\",\"topK\":5,\"metadataFilter\":{\"project\":\"agent-global-context\"}}"
```

## Get Trace

```powershell
curl http://localhost:8080/api/v1/traces/trace_example
```
```

- [ ] **Step 4: Run full test suite**

Run:

```powershell
mvn -f knowledge-agent-api/pom.xml test
```

Expected: `BUILD SUCCESS`.

- [ ] **Step 5: Commit end-to-end coverage and docs**

```powershell
git add README.md knowledge-agent-api/src/test/java/com/xwf/agentknowledge/query/api/QueryApiIntegrationTest.java
git commit -m "test: cover v0.1 rag api loop"
```

---

### Task 9: Final Verification

**Files:**
- Modify only if verification finds a concrete issue.

- [ ] **Step 1: Check working tree**

Run:

```powershell
git status --short --branch
```

Expected: no tracked-file changes. `.idea/` may remain untracked unless `.gitignore` was added before status.

- [ ] **Step 2: Run full tests**

Run:

```powershell
mvn -f knowledge-agent-api/pom.xml test
```

Expected: `BUILD SUCCESS`.

- [ ] **Step 3: Start the local API**

Run:

```powershell
mvn -f knowledge-agent-api/pom.xml spring-boot:run
```

Expected: application starts on port `8080` and logs that Tomcat started.

- [ ] **Step 4: Smoke test import and query manually**

In another terminal, run:

```powershell
curl -X POST http://localhost:8080/api/v1/documents/import `
  -H "Content-Type: application/json" `
  -d "{\"sourcePath\":\"docs/architecture.zh.md\",\"title\":\"Architecture\",\"content\":\"# Architecture\n\n## Commit Policy\n\nExplicit memory requests can usually be committed directly.\",\"metadata\":{\"project\":\"agent-global-context\",\"sourceType\":\"markdown\"}}"
```

Expected response includes:

```json
{
  "chunkCount": 1,
  "status": "IMPORTED"
}
```

Then run:

```powershell
curl -X POST http://localhost:8080/api/v1/query `
  -H "Content-Type: application/json" `
  -d "{\"query\":\"memory commit\",\"topK\":5,\"metadataFilter\":{\"project\":\"agent-global-context\"}}"
```

Expected response includes non-empty `answer`, at least one `citation`, and a `traceId` starting with `trace_`.

- [ ] **Step 5: Stop the local API**

Press `Ctrl+C` in the terminal running Spring Boot.

- [ ] **Step 6: Commit verification fixes if needed**

If Step 4 reveals a concrete issue, fix only that issue, rerun:

```powershell
mvn -f knowledge-agent-api/pom.xml test
```

Then commit:

```powershell
git add knowledge-agent-api README.md
git commit -m "fix: complete v0.1 smoke path"
```

If no issue is found, do not create a commit.

## Self-Review Notes

- Spec coverage: Tasks cover Spring Boot scaffold, ingest, Markdown parsing, chunking, metadata persistence, deterministic query retrieval, answer draft, citations, trace API, tests, and README usage examples.
- Deferred scope: Real LLM calls, embeddings, vector stores, memory, eval, UI, async indexing, and distributed concerns are intentionally outside v0.1 and are not represented as implementation tasks.
- Type consistency: The plan uses `KnowledgeDocument`, `DocumentChunk`, `ParsedMarkdownSection`, `KnowledgeQueryRequest`, `KnowledgeQueryResponse`, `Citation`, `RetrievedChunk`, and trace types consistently across service, controller, and tests.
