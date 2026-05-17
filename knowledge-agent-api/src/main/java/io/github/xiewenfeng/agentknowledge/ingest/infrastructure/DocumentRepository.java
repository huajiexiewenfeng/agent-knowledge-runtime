package io.github.xiewenfeng.agentknowledge.ingest.infrastructure;

import io.github.xiewenfeng.agentknowledge.ingest.domain.KnowledgeDocument;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentRepository extends JpaRepository<KnowledgeDocument, Long> {
}
