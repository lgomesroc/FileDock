package com.filedock.document;

import com.filedock.document.dto.CreateDocumentRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    private final DocumentRepository documentRepository;
    private final DocumentService documentService;

    public DocumentController(
            DocumentRepository documentRepository,
            DocumentService documentService) {
        this.documentRepository = documentRepository;
        this.documentService = documentService;
    }

    @GetMapping
    public List<Document> findAll() {
        return documentRepository.findAll();
    }

    @GetMapping("/{id}")
    public Document findById(@PathVariable Long id) {
        return documentService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Document create(@RequestBody CreateDocumentRequest request) {
        return documentService.create(request);
    }
}
