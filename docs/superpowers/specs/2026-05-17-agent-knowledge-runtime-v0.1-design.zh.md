# Agent Knowledge Runtime v0.1 设计文档

## 状态

本设计已在 2026-05-17 确认，可进入 v0.1 实施计划阶段。

## 项目定位

`agent-knowledge-runtime` 是一个基于 Java/Spring Boot 的 Agent 知识运行层后端项目。它最终会发展成一个真实的、可展示的作品集项目；当前 v0.1 阶段只聚焦最小可靠的 RAG 后端闭环。

这个项目不是普通聊天 demo，也不是单纯的向量数据库示例。它的核心目标是为未来的 coding agent 和 knowledge agent 提供可靠的知识导入、检索、引用和 trace 能力。

## v0.1 目标

构建一个可运行的 RAG 后端最小闭环：

```text
Markdown 文档导入
-> 文档解析
-> chunk 切分
-> metadata 持久化
-> top-k 查询检索
-> 生成带 citation 的 answer draft
-> 记录 trace
```

v0.1 要证明系统能回答这些工程问题：

- 知识从哪里进入系统？
- 原始材料如何被切成 chunk？
- metadata 如何保留来源和可追溯信息？
- 查询时如何找到相关 chunk？
- 回答如何引用证据？
- 当回答质量不好时，如何通过 trace 定位问题？

## v0.1 明确不做

v0.1 不实现：

- 真实 LLM 调用。
- 真实 embedding 或向量数据库检索。
- 前端 UI。
- 完整 memory candidate / review / commit / recall 流程。
- eval runner。
- 分布式队列或异步索引。
- 多服务架构。

但设计上要为这些后续能力预留扩展点。

## 推荐方案

v0.1 采用纯后端 API MVP，同时保留少量未来 runtime 结构。

v0.1 实现：

- `ingest`
- `query`
- `common.trace`
- `common.error`

v0.1 只预留，不完整实现：

- `memory`
- `eval`
- model adapter
- vector store adapter

## 技术栈

- Java 17 或 Java 21。
- Spring Boot 3.x。
- Maven。
- H2 作为本地 demo 持久化数据库。
- JUnit 用于测试。

Spring AI 在 v0.1 阶段不是必须项。第一版应该先定义本地 adapter 接口，使后续 Spring AI、OpenAI、DeepSeek、pgvector、Milvus 或 Qdrant 可以接入，而不需要重写应用层逻辑。

## 仓库和模块结构

仓库名：

```text
agent-knowledge-runtime
```

后端模块名：

```text
knowledge-agent-api
```

初始结构：

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

分层含义：

- `api`：Controller、request DTO、response DTO。
- `application`：用例编排服务。
- `domain`：核心领域模型和规则。
- `infrastructure`：持久化、解析器、repository、具体 adapter。

依赖方向：

```text
api -> application -> domain
application -> infrastructure interfaces
infrastructure -> concrete implementation
```

v0.1 保持单个 Spring Boot 模块。等边界被验证稳定后，再考虑拆成多 Maven module。

## 核心组件

### Ingest

职责：

- 接收 Markdown 内容。
- 解析 heading 和正文区块。
- 将内容切分为 chunk。
- 为每个 chunk 附加 metadata。
- 持久化 document 和 chunk。

候选类：

- `MarkdownDocumentController`
- `ImportDocumentService`
- `MarkdownParser`
- `ChunkingService`
- `DocumentRepository`
- `ChunkRepository`

### Query

职责：

- 接收用户查询和可选 metadata filter。
- 检索 top-k 相关 chunk。
- 根据检索结果构建 citation。
- 组合确定性的 answer draft。
- 记录并返回 trace id。

候选类：

- `KnowledgeQueryController`
- `QueryKnowledgeService`
- `ChunkRetriever`
- `AnswerComposer`
- `CitationBuilder`

### Trace

职责：

- 为查询请求创建 trace id。
- 记录检索输入和被选中的 chunk。
- 保留足够信息，用于调试 retrieval 和 citation 行为。

候选类：

- `TraceId`
- `TraceEvent`
- `TraceRecorder`

## 核心数据模型

### Document

表示一份被导入的源文档。

字段：

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

表示一个可检索的知识单元。

字段：

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

表示一次查询请求。

字段：

```text
query
topK
metadataFilter
```

### Citation

表示回答中引用的证据来源。

字段：

```text
chunkId
documentId
sourcePath
heading
chunkIndex
snippet
```

### Trace

表示一次 RAG 请求执行过程。

字段：

```text
traceId
query
retrievedChunkIds
events
latencyMs
createdAt
```

metadata 示例：

```json
{
  "project": "agent-knowledge-runtime",
  "sourceType": "markdown",
  "tags": ["architecture"],
  "language": "zh"
}
```

## API 设计

v0.1 提供四个 API。

### 导入文档

```text
POST /api/v1/documents/import
```

请求：

```json
{
  "sourcePath": "docs/architecture.zh.md",
  "title": "Architecture",
  "content": "# Architecture\n\n..."
}
```

响应：

```json
{
  "documentId": "doc_001",
  "chunkCount": 12,
  "status": "IMPORTED"
}
```

### 查询文档

```text
GET /api/v1/documents/{documentId}
```

返回文档 metadata，必要时可以返回 chunk 摘要信息。

### 查询知识库

```text
POST /api/v1/query
```

请求：

```json
{
  "query": "memory commit 的规则是什么？",
  "topK": 5,
  "metadataFilter": {
    "project": "agent-global-context"
  }
}
```

响应：

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

v0.1 的 `answer` 不由 LLM 生成，而是由 `AnswerComposer` 根据检索到的 chunk 组合出结构化草稿。这样可以优先验证 retrieval、citation 和 trace 行为。

### 查询 Trace

```text
GET /api/v1/traces/{traceId}
```

返回一次查询请求的 trace 记录。

## 检索策略

v0.1 使用确定性的关键词评分，不使用向量检索。

基本流程：

```text
normalize query
-> 比较 query terms 与 chunk heading/content
-> 如有 metadata filter，则先过滤
-> 计算 chunk score
-> 按 score 排序
-> 返回 topK
```

这个方案刻意保持简单。它容易测试、容易解释，后续也可以隐藏在 `ChunkRetriever` 或 `VectorStore` 抽象后面，被 embedding/vector search 替换。

## 错误处理

使用统一错误响应：

```json
{
  "errorCode": "DOCUMENT_NOT_FOUND",
  "message": "Document not found",
  "traceId": "trace_abc123"
}
```

初始错误码：

- `INVALID_REQUEST`
- `DOCUMENT_NOT_FOUND`
- `DOCUMENT_IMPORT_FAILED`
- `QUERY_EMPTY`
- `NO_CHUNKS_FOUND`
- `TRACE_NOT_FOUND`
- `INTERNAL_ERROR`

导入或查询失败时，错误信息要能解释失败发生在哪一步。

## 测试策略

v0.1 应覆盖最小闭环的关键测试：

- `MarkdownParserTest`：验证 heading 和正文区块解析。
- `ChunkingServiceTest`：验证 chunk 带有 source path、heading、index。
- `QueryKnowledgeServiceTest`：验证能返回 top-k chunks 和 citations。
- `ImportDocumentIntegrationTest`：通过 API 完成文档导入。
- `QueryApiIntegrationTest`：导入内容后查询，得到 answer、citations 和 trace id。

这些测试很重要，因为它们能证明后端是稳定系统，而不是一次性 demo。

## 演进路线

v0.1 之后建议路线：

```text
v0.2: 接入 Spring AI embedding 和 vector store adapter。
v0.3: 支持 Markdown 文件夹导入和文档更新重建索引。
v0.4: 实现 memory candidate / review / commit / recall。
v0.5: 加入 eval golden cases、failure cases 和 trace report。
v0.6: 加入异步导入任务、幂等和任务状态。
v0.7: 增强权限、项目隔离和可观测性。
```

## 最终决策

v0.1 将实现一个小而真实的 RAG 后端闭环，技术栈为 Java 和 Spring Boot。它优先保证模块边界清晰、检索可控、引用可追溯、trace 可调试，并提供基础测试。真实 LLM、embedding、向量检索、memory、eval 和 UI 都延后，等核心后端闭环可靠后再逐步加入。
