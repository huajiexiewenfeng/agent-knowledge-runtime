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
