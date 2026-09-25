package com.filedock.document;

import com.filedock.document.dto.DocumentResponse;
import com.filedock.exception.ResourceNotFoundException;
import com.filedock.storage.FileStorageService;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DocumentServiceTest {

    private final DocumentRepository documentRepository =
            mock(DocumentRepository.class);

    private final FileStorageService fileStorageService =
            mock(FileStorageService.class);

    private final DocumentService documentService =
            new DocumentService(
                    documentRepository,
                    fileStorageService
            );

    @Test
    void shouldDeleteDocumentAndStoredFile() throws Exception {

        Document document = createDocument();

        when(documentRepository.findById(1L))
                .thenReturn(Optional.of(document));

        documentService.delete(1L);

        verify(fileStorageService)
                .delete("documento-1.pdf");

        verify(documentRepository)
                .delete(document);
    }

    @Test
    void shouldNotDeleteDocumentWhenStoredFileDeletionFails()
            throws Exception {

        Document document = createDocument();

        when(documentRepository.findById(1L))
                .thenReturn(Optional.of(document));

        doThrow(
                new IOException("Falha no armazenamento")
        ).when(fileStorageService)
                .delete("documento-1.pdf");

        assertThrows(
                IllegalStateException.class,
                () -> documentService.delete(1L)
        );

        verify(fileStorageService)
                .delete("documento-1.pdf");

        verify(documentRepository, never())
                .delete(document);
    }

    @Test
    void shouldThrowWhenDocumentDoesNotExist()
            throws IOException {

        when(documentRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> documentService.delete(999L)
        );

        verify(fileStorageService, never())
                .delete(any());

        verify(documentRepository, never())
                .delete(any(Document.class));
    }

    @Test
    void shouldReplaceDocumentFile() throws Exception {

        Document document = createDocument();

        MockMultipartFile newFile =
                new MockMultipartFile(
                        "file",
                        "novo-documento.pdf",
                        "application/pdf",
                        "novo conteúdo".getBytes()
                );

        when(documentRepository.findById(1L))
                .thenReturn(Optional.of(document));

        when(fileStorageService.store(newFile))
                .thenReturn("novo-documento.pdf");

        when(documentRepository.save(document))
                .thenReturn(document);

        DocumentResponse response =
                documentService.replaceFile(
                        1L,
                        newFile
                );

        assertEquals(
                "novo-documento.pdf",
                document.getFileName()
        );

        assertEquals(
                "application/pdf",
                document.getContentType()
        );

        assertEquals(
                14L,
                document.getFileSize()
        );

        assertEquals(
                "novo-documento.pdf",
                document.getStoragePath()
        );

        assertEquals(
                ProcessingStatus.PENDING,
                document.getProcessingStatus()
        );

        assertNull(document.getProcessedAt());

        assertEquals(
                "novo-documento.pdf",
                response.storagePath()
        );

        verify(fileStorageService)
                .store(newFile);

        verify(documentRepository)
                .save(document);

        verify(fileStorageService)
                .delete("documento-1.pdf");
    }

    @Test
    void shouldNotReplaceFileWhenDocumentDoesNotExist()
            throws Exception {

        MockMultipartFile newFile =
                new MockMultipartFile(
                        "file",
                        "novo-documento.pdf",
                        "application/pdf",
                        "novo conteúdo".getBytes()
                );

        when(documentRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> documentService.replaceFile(
                        999L,
                        newFile
                )
        );

        verify(fileStorageService, never())
                .store(any());

        verify(documentRepository, never())
                .save(any(Document.class));
    }

    @Test
    void shouldNotReplaceFileWhenFileIsEmpty()
            throws Exception {

        MockMultipartFile emptyFile =
                new MockMultipartFile(
                        "file",
                        "arquivo.pdf",
                        "application/pdf",
                        new byte[0]
                );

        assertThrows(
                IllegalArgumentException.class,
                () -> documentService.replaceFile(
                        1L,
                        emptyFile
                )
        );

        verify(fileStorageService, never())
                .store(any());

        verify(documentRepository, never())
                .save(any(Document.class));
    }

    @Test
    void shouldNotSaveDocumentWhenNewFileCannotBeStored()
            throws Exception {

        Document document = createDocument();

        MockMultipartFile newFile =
                new MockMultipartFile(
                        "file",
                        "novo-documento.pdf",
                        "application/pdf",
                        "novo conteúdo".getBytes()
                );

        when(documentRepository.findById(1L))
                .thenReturn(Optional.of(document));

        when(fileStorageService.store(newFile))
                .thenThrow(
                        new IOException(
                                "Falha no armazenamento"
                        )
                );

        assertThrows(
                IllegalStateException.class,
                () -> documentService.replaceFile(
                        1L,
                        newFile
                )
        );

        verify(fileStorageService)
                .store(newFile);

        verify(documentRepository, never())
                .save(any(Document.class));

        verify(fileStorageService, never())
                .delete("documento-1.pdf");
    }

    private Document createDocument() {

        Document document = new Document();

        document.setTitle("Documento de teste");
        document.setDescription("Descrição");
        document.setFileName("documento-1.pdf");
        document.setContentType("application/pdf");
        document.setFileSize(1024L);
        document.setStoragePath("documento-1.pdf");
        document.setProcessingStatus(
                ProcessingStatus.PROCESSED
        );

        return document;
    }
}
