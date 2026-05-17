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
