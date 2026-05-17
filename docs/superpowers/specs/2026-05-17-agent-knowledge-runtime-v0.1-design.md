# Agent Knowledge Runtime v0.1 Design

## Status

Approved for v0.1 planning on 2026-05-17.

## Project Positioning

`agent-knowledge-runtime` is a Java/Spring Boot backend runtime for agent knowledge systems. It is intended to become a real portfolio-grade system, while v0.1 focuses on the smallest reliable RAG backend loop.

This project is not a generic chatbot demo and not just a vector database example. Its core purpose is to provide reliable knowledge ingestion, retrieval, citation, and trace capabilities for future coding agents and knowledge agents.

## v0.1 Goal

Build a runnable RAG backend minimum loop:

```text
Markdown document import
-> document parsing
-> chunking
-> metadata persistence
-> top-k query retrieval
-> answer draft with citations
-> trace recording
```

v0.1 should prove that the system can answer these engineering questions:

- Where does knowledge come from?
- How is source material split into chunks?
- How is metadata preserved for traceability?
- How does query retrieval find relevant chunks?
- How does an answer cite its evidence?
- How can a failed or poor answer be traced?

## Explicit Non-Goals for v0.1

v0.1 will not implement:

- Real LLM calls.
- Real embedding or vector database search.
- Frontend UI.
- Full memory candidate/review/commit/recall flow.
- Eval runner.
- Distributed queue or async indexing.
- Multi-service architecture.

The design should still leave extension points for these later.

## Recommended Approach

Use a pure backend API MVP with a small amount of future runtime structure.

Implemented in v0.1:

- `ingest`
- `query`
- `common.trace`
- `common.error`

Reserved but not fully implemented in v0.1:

- `memory`
- `eval`
- model adapter
- vector store adapter

## Technology Stack

- Java 17 or Java 21.
- Spring Boot 3.x.
- Maven.
- H2 for local demo persistence.
- JUnit for tests.

Spring AI is not required in v0.1. The first version should define local adapter interfaces so Spring AI, OpenAI, DeepSeek, pgvector, Milvus, or Qdrant can be integrated later without rewriting application logic.

## Repository and Module Structure

Repository:

```text
agent-knowledge-runtime
```

Backend module:

```text
knowledge-agent-api
```

Initial structure:

```text
agent-knowledge-runtime
  knowledge-agent-api
    src/main/java/io/github/xiewenfeng/agentknowledge
      AgentKnowledgeApplication.java

      common
        error
        trace
        model

      ingest
        api
        application
        domain
        infrastructure

      query
        api
        application
        domain
        infrastructure

      memory
        package-info.java

      eval
        package-info.java
```

Layer intent:

- `api`: controllers plus request and response DTOs.
- `application`: use-case orchestration services.
- `domain`: core domain models and rules.
- `infrastructure`: persistence, parsers, repositories, and concrete adapters.

Dependency direction:

```text
api -> application -> domain
application -> infrastructure interfaces
infrastructure -> concrete implementation
```

v0.1 should stay as a single Spring Boot module. Multi-module Maven can be introduced after the boundaries are proven.

## Core Components

### Ingest

Responsibilities:

- Accept Markdown content.
- Parse headings and body sections.
- Split content into chunks.
- Attach metadata to each chunk.
- Persist documents and chunks.

Candidate classes:

- `MarkdownDocumentController`
- `ImportDocumentService`
- `MarkdownParser`
- `ChunkingService`
- `DocumentRepository`
- `ChunkRepository`

### Query

Responsibilities:

- Accept user query and optional metadata filter.
- Retrieve top-k relevant chunks.
- Build citation objects from retrieved chunks.
- Compose a deterministic answer draft.
- Record and return a trace id.

Candidate classes:

- `KnowledgeQueryController`
- `QueryKnowledgeService`
- `ChunkRetriever`
- `AnswerComposer`
- `CitationBuilder`

### Trace

Responsibilities:

- Create a trace id for query requests.
- Record retrieval inputs and selected chunks.
- Preserve enough information to debug retrieval and citation behavior.

Candidate classes:

- `TraceId`
- `TraceEvent`
- `TraceRecorder`

## Core Data Model

### Document

Represents an imported source document.

Fields:

```text
id
sourcePath
title
contentHash
importedAt
updatedAt
status
```

### Chunk

Represents a retrievable knowledge unit.

Fields:

```text
id
documentId
sourcePath
heading
chunkIndex
content
contentHash
metadataJson
createdAt
```

### KnowledgeQuery

Represents a query request.

Fields:

```text
query
topK
metadataFilter
```

### Citation

Represents evidence attached to an answer.

Fields:

```text
chunkId
documentId
sourcePath
heading
chunkIndex
snippet
```

### Trace

Represents one RAG request execution.

Fields:

```text
traceId
query
retrievedChunkIds
events
latencyMs
createdAt
```

Example metadata:

```json
{
  "project": "agent-knowledge-runtime",
  "sourceType": "markdown",
  "tags": ["architecture"],
  "language": "zh"
}
```

## API Design

v0.1 exposes four API endpoints.

### Import Document

```text
POST /api/v1/documents/import
```

Request:

```json
{
  "sourcePath": "docs/architecture.zh.md",
  "title": "Architecture",
  "content": "# Architecture\n\n..."
}
```

Response:

```json
{
  "documentId": "doc_001",
  "chunkCount": 12,
  "status": "IMPORTED"
}
```

### Get Document

```text
GET /api/v1/documents/{documentId}
```

Returns document metadata and optionally chunk summary information.

### Query Knowledge

```text
POST /api/v1/query
```

Request:

```json
{
  "query": "memory commit 的规则是什么？",
  "topK": 5,
  "metadataFilter": {
    "project": "agent-global-context"
  }
}
```

Response:

```json
{
  "answer": "根据检索到的内容，memory commit 需要...",
  "citations": [
    {
      "chunkId": "chunk_001",
      "sourcePath": "docs/architecture.zh.md",
      "heading": "Commit Policy",
      "snippet": "Explicit memory requests can usually be committed directly..."
    }
  ],
  "traceId": "trace_abc123"
}
```

The v0.1 `answer` is not generated by an LLM. It is produced by `AnswerComposer` from retrieved chunks so retrieval, citation, and trace behavior can be validated first.

### Get Trace

```text
GET /api/v1/traces/{traceId}
```

Returns the recorded trace for a query request.

## Retrieval Strategy

v0.1 uses deterministic keyword scoring rather than vector search.

Basic flow:

```text
normalize query
-> compare query terms with chunk heading and content
-> apply metadata filter if provided
-> score chunks
-> sort by score
-> return topK
```

This approach is intentionally simple. It is easy to test, easy to explain, and can later be replaced behind a `ChunkRetriever` or `VectorStore` abstraction.

## Error Handling

Use a unified error response:

```json
{
  "errorCode": "DOCUMENT_NOT_FOUND",
  "message": "Document not found",
  "traceId": "trace_abc123"
}
```

Initial error codes:

- `INVALID_REQUEST`
- `DOCUMENT_NOT_FOUND`
- `DOCUMENT_IMPORT_FAILED`
- `QUERY_EMPTY`
- `NO_CHUNKS_FOUND`
- `TRACE_NOT_FOUND`
- `INTERNAL_ERROR`

Failures should be structured enough to explain what happened during import or query.

## Testing Strategy

v0.1 should include focused tests for the minimum loop:

- `MarkdownParserTest`: parses headings and body sections.
- `ChunkingServiceTest`: creates chunks with source path, heading, and index.
- `QueryKnowledgeServiceTest`: returns top-k chunks and citations.
- `ImportDocumentIntegrationTest`: imports a document through the API.
- `QueryApiIntegrationTest`: imports content, queries it, and receives answer, citations, and trace id.

These tests are important because they prove the backend is a stable system rather than a throwaway demo.

## Evolution Path

Suggested roadmap after v0.1:

```text
v0.2: Spring AI embedding and vector store adapter.
v0.3: Markdown folder import and document update/reindex flow.
v0.4: memory candidate / review / commit / recall.
v0.5: eval golden cases, failure cases, and trace report.
v0.6: async import jobs, idempotency, and task status.
v0.7: permissions, project isolation, and observability improvements.
```

## Final Decision

v0.1 will implement a small but real RAG backend loop using Java and Spring Boot. It will prioritize clean module boundaries, deterministic retrieval, citations, traceability, and tests. Real LLMs, embeddings, vector search, memory, eval, and UI are intentionally deferred until the core backend loop is reliable.
