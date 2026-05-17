package io.github.xiewenfeng.agentknowledge.ingest.domain;

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
