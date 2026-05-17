package io.github.xiewenfeng.agentknowledge.ingest.api;

import io.github.xiewenfeng.agentknowledge.ingest.application.ImportDocumentService;
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
