package com.filedock.document;

import com.filedock.document.dto.CreateDocumentRequest;
import com.filedock.document.dto.DocumentDownload;
import com.filedock.document.dto.DocumentResponse;
import com.filedock.document.dto.UpdateDocumentRequest;
import com.filedock.exception.ResourceNotFoundException;
import com.filedock.storage.FileStorageService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
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

    public DocumentResponse upload(
            String title,
            String description,
            MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException(
                    "O arquivo é obrigatório."
            );
        }

        try {
            String storagePath = fileStorageService.store(file);

            LocalDateTime now = LocalDateTime.now();

            Document document = new Document();

            document.setTitle(title);
            document.setDescription(description);
            document.setFileName(file.getOriginalFilename());
            document.setContentType(file.getContentType());
            document.setFileSize(file.getSize());
            document.setStoragePath(storagePath);
            document.setCreatedAt(now);
            document.setUpdatedAt(now);
            document.setProcessingStatus(ProcessingStatus.PENDING);

            return toResponse(documentRepository.save(document));

        } catch (IOException exception) {
            throw new IllegalStateException(
                    "Não foi possível armazenar o arquivo.",
                    exception
            );
        }
    }

    public DocumentResponse findById(Long id) {
        Document document = findDocumentById(id);

        return toResponse(document);
    }

    public DocumentDownload download(Long id) {

        Document document = findDocumentById(id);

        if (document.getStoragePath() == null
                || document.getStoragePath().isBlank()) {

            throw new IllegalStateException(
                    "O documento não possui arquivo armazenado."
            );
        }

        try {
            Path filePath = fileStorageService.load(
                    document.getStoragePath()
            );

            Resource resource = new UrlResource(
                    filePath.toUri()
            );

            if (!resource.exists() || !resource.isReadable()) {
                throw new IllegalStateException(
                        "O arquivo não está disponível para download."
                );
            }

            String contentType = document.getContentType();

            if (contentType == null || contentType.isBlank()) {
                contentType = "application/octet-stream";
            }

            return new DocumentDownload(
                    resource,
                    document.getFileName(),
                    contentType
            );

        } catch (MalformedURLException exception) {
            throw new IllegalStateException(
                    "Não foi possível acessar o arquivo.",
                    exception
            );

        } catch (IOException exception) {
            throw new IllegalStateException(
                    "Não foi possível carregar o arquivo.",
                    exception
            );
        }
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

    public void delete(Long id) {

        Document document = findDocumentById(id);

        try {
            fileStorageService.delete(document.getStoragePath());

        } catch (IOException exception) {
            throw new IllegalStateException(
                    "Não foi possível excluir o arquivo armazenado.",
                    exception
            );
        }

        documentRepository.delete(document);
    }

    public DocumentResponse replaceFile(
            Long id,
            MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException(
                    "O arquivo é obrigatório."
            );
        }

        Document document = findDocumentById(id);

        String oldStoragePath = document.getStoragePath();

        try {
            String newStoragePath = fileStorageService.store(file);

            document.setFileName(file.getOriginalFilename());
            document.setContentType(file.getContentType());
            document.setFileSize(file.getSize());
            document.setStoragePath(newStoragePath);
            document.setUpdatedAt(LocalDateTime.now());
            document.setProcessingStatus(ProcessingStatus.PENDING);
            document.setProcessedAt(null);

            DocumentResponse response =
                    toResponse(documentRepository.save(document));

            if (oldStoragePath != null && !oldStoragePath.isBlank()) {
                fileStorageService.delete(oldStoragePath);
            }

            return response;

        } catch (IOException exception) {
            throw new IllegalStateException(
                    "Não foi possível substituir o arquivo.",
                    exception
            );
        }
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
