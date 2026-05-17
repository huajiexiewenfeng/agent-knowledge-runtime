package io.github.xiewenfeng.agentknowledge.ingest.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.xiewenfeng.agentknowledge.common.error.ErrorCode;
import io.github.xiewenfeng.agentknowledge.common.error.ResourceNotFoundException;
import io.github.xiewenfeng.agentknowledge.ingest.api.DocumentResponse;
import io.github.xiewenfeng.agentknowledge.ingest.api.ImportDocumentRequest;
import io.github.xiewenfeng.agentknowledge.ingest.api.ImportDocumentResponse;
import io.github.xiewenfeng.agentknowledge.ingest.domain.DocumentChunk;
import io.github.xiewenfeng.agentknowledge.ingest.domain.KnowledgeDocument;
import io.github.xiewenfeng.agentknowledge.ingest.domain.ParsedMarkdownSection;
import io.github.xiewenfeng.agentknowledge.ingest.infrastructure.ChunkRepository;
import io.github.xiewenfeng.agentknowledge.ingest.infrastructure.DocumentRepository;
import io.github.xiewenfeng.agentknowledge.ingest.infrastructure.MarkdownParser;
import io.github.xiewenfeng.agentknowledge.ingest.infrastructure.SimpleChunkingService;
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
