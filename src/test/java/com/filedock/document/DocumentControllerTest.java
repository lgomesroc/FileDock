package com.filedock.document;

import com.filedock.document.dto.CreateDocumentRequest;
import com.filedock.document.dto.DocumentDownload;
import com.filedock.document.dto.DocumentResponse;
import com.filedock.document.dto.UpdateDocumentRequest;
import com.filedock.storage.FileStorageService;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

class DocumentControllerTest {

    private final DocumentService documentService =
            mock(DocumentService.class);

    private final FileStorageService fileStorageService =
            mock(FileStorageService.class);

    private final MockMvc mockMvc =
            org.springframework.test.web.servlet.setup.MockMvcBuilders
                    .standaloneSetup(
                            new DocumentController(documentService)
                    )
                    .setControllerAdvice(
                            new com.filedock.exception.GlobalExceptionHandler()
                    )
                    .build();

    @Test
    void shouldReturnAllDocuments() throws Exception {

        when(documentService.findAll())
                .thenReturn(List.of());

        mockMvc.perform(get("/api/documents"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void shouldReturnDocumentById() throws Exception {

        DocumentResponse response = createResponse();

        when(documentService.findById(1L))
                .thenReturn(response);

        mockMvc.perform(get("/api/documents/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title")
                        .value("Documento de teste"));
    }

    @Test
    void shouldCreateDocument() throws Exception {

        DocumentResponse response = createResponse();

        when(documentService.create(any(CreateDocumentRequest.class)))
                .thenReturn(response);

        mockMvc.perform(
                        post("/api/documents")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "title": "Documento de teste",
                                          "description": "Descrição",
                                          "fileName": "teste.pdf",
                                          "contentType": "application/pdf",
                                          "fileSize": 1024
                                        }
                                        """)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title")
                        .value("Documento de teste"));
    }

    @Test
    void shouldUploadDocument() throws Exception {

        DocumentResponse response = createResponse();

        when(documentService.upload(
                eq("Documento de upload"),
                eq("Descrição"),
                any()
        )).thenReturn(response);

        mockMvc.perform(
                        multipart("/api/documents/upload")
                                .file(
                                        "file",
                                        "conteudo do arquivo"
                                                .getBytes(
                                                        StandardCharsets.UTF_8
                                                )
                                )
                                .param(
                                        "title",
                                        "Documento de upload"
                                )
                                .param(
                                        "description",
                                        "Descrição"
                                )
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void shouldUpdateDocument() throws Exception {

        DocumentResponse response = createResponse();

        when(documentService.update(
                eq(1L),
                any(UpdateDocumentRequest.class)
        )).thenReturn(response);

        mockMvc.perform(
                        put("/api/documents/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "title": "Documento atualizado",
                                          "description": "Nova descrição",
                                          "fileName": "novo.pdf",
                                          "contentType": "application/pdf",
                                          "fileSize": 2048
                                        }
                                        """)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void shouldReturnNotFoundWhenDocumentDoesNotExist()
            throws Exception {

        when(documentService.findById(999L))
                .thenThrow(
                        new com.filedock.exception.ResourceNotFoundException(
                                "Documento não encontrado: 999"
                        )
                );

        mockMvc.perform(get("/api/documents/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnBadRequestWhenCreateRequestIsInvalid()
            throws Exception {

        mockMvc.perform(
                        post("/api/documents")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "title": "",
                                          "description": "",
                                          "fileName": "",
                                          "contentType": "",
                                          "fileSize": 0
                                        }
                                        """)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldDownloadDocument() throws Exception {

        byte[] fileContent =
                "Teste real de download"
                        .getBytes(StandardCharsets.UTF_8);

        DocumentDownload download = new DocumentDownload(
                new ByteArrayResource(fileContent),
                "teste-download.txt",
                "text/plain"
        );

        when(documentService.download(1L))
                .thenReturn(download);

        mockMvc.perform(get("/api/documents/1/download"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/plain"))
                .andExpect(header().string(
                        "Content-Disposition",
                        "attachment; filename=\"teste-download.txt\""
                ))
                .andExpect(content().bytes(fileContent));
    }

    @Test
    void shouldDeleteDocument() throws Exception {

        mockMvc.perform(
                        delete("/api/documents/1")
                )
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReplaceDocumentFile() throws Exception {

        DocumentResponse response = createResponse();

        when(documentService.replaceFile(
                eq(1L),
                any()
        )).thenReturn(response);

        mockMvc.perform(
                        multipart("/api/documents/1/file")
                                .file(
                                        "file",
                                        "novo conteúdo"
                                                .getBytes(
                                                        StandardCharsets.UTF_8
                                                )
                                )
                                .with(request -> {
                                    request.setMethod("PUT");
                                    return request;
                                })
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    private DocumentResponse createResponse() {

        return new DocumentResponse(
                1L,
                "Documento de teste",
                "Descrição",
                "teste.pdf",
                "application/pdf",
                1024L,
                "documento-1.pdf",
                LocalDateTime.now(),
                LocalDateTime.now(),
                ProcessingStatus.PENDING,
                null
        );
    }
}
