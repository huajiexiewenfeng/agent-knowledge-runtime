package io.github.xiewenfeng.agentknowledge.ingest.domain;

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
