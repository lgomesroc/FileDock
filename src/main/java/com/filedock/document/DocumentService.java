package com.filedock.document;

import com.filedock.document.dto.CreateDocumentRequest;
import com.filedock.document.dto.UpdateDocumentRequest;
import com.filedock.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class DocumentService {

    private final DocumentRepository documentRepository;

    public DocumentService(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    public Document create(CreateDocumentRequest request) {
        Document document = new Document();

        document.setTitle(request.title());
        document.setDescription(request.description());
        document.setFileName(request.fileName());
        document.setContentType(request.contentType());
        document.setFileSize(request.fileSize());
        document.setCreatedAt(LocalDateTime.now());
        document.setUpdatedAt(LocalDateTime.now());
        document.setProcessingStatus(ProcessingStatus.PENDING);

        return documentRepository.save(document);
    }

    public Document findById(Long id) {
        return documentRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Documento não encontrado: " + id
                        )
                );
    }

    public Document update(Long id, UpdateDocumentRequest request) {
        Document document = findById(id);

        document.setTitle(request.title());
        document.setDescription(request.description());
        document.setFileName(request.fileName());
        document.setContentType(request.contentType());
        document.setFileSize(request.fileSize());
        document.setUpdatedAt(LocalDateTime.now());

        return documentRepository.save(document);
    }
}
