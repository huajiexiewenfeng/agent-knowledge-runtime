package io.github.xiewenfeng.agentknowledge.query.api;

import io.github.xiewenfeng.agentknowledge.query.application.QueryKnowledgeService;
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
