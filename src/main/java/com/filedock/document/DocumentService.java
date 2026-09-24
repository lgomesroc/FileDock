package com.filedock.document;

import com.filedock.document.dto.CreateDocumentRequest;
import com.filedock.document.dto.DocumentResponse;
import com.filedock.document.dto.UpdateDocumentRequest;
import com.filedock.exception.ResourceNotFoundException;
import com.filedock.storage.FileStorageService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final FileStorageService fileStorageService;

    public DocumentService(
            DocumentRepository documentRepository,
            FileStorageService fileStorageService) {

        this.documentRepository = documentRepository;
        this.fileStorageService = fileStorageService;
    }

    public List<DocumentResponse> findAll() {
        return documentRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public DocumentResponse create(CreateDocumentRequest request) {
        Document document = new Document();

        document.setTitle(request.title());
        document.setDescription(request.description());
        document.setFileName(request.fileName());
        document.setContentType(request.contentType());
        document.setFileSize(request.fileSize());
        document.setCreatedAt(LocalDateTime.now());
        document.setUpdatedAt(LocalDateTime.now());
        document.setProcessingStatus(ProcessingStatus.PENDING);

        return toResponse(documentRepository.save(document));
    }

    public DocumentResponse findById(Long id) {
        Document document = findDocumentById(id);

        return toResponse(document);
    }

    public DocumentResponse update(
            Long id,
            UpdateDocumentRequest request) {

        Document document = findDocumentById(id);

        document.setTitle(request.title());
        document.setDescription(request.description());
        document.setFileName(request.fileName());
        document.setContentType(request.contentType());
        document.setFileSize(request.fileSize());
        document.setUpdatedAt(LocalDateTime.now());

        return toResponse(documentRepository.save(document));
    }

    private Document findDocumentById(Long id) {
        return documentRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Documento não encontrado: " + id
                        )
                );
    }

    private DocumentResponse toResponse(Document document) {
        return new DocumentResponse(
                document.getId(),
                document.getTitle(),
                document.getDescription(),
                document.getFileName(),
                document.getContentType(),
                document.getFileSize(),
                document.getStoragePath(),
                document.getCreatedAt(),
                document.getUpdatedAt(),
                document.getProcessingStatus(),
                document.getProcessedAt()
        );
    }
}
