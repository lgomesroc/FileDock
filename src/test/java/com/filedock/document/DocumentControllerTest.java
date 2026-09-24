package com.filedock.document;

import com.filedock.document.dto.CreateDocumentRequest;
import com.filedock.document.dto.DocumentResponse;
import com.filedock.document.dto.UpdateDocumentRequest;
import com.filedock.exception.GlobalExceptionHandler;
import com.filedock.exception.ResourceNotFoundException;
import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DocumentController.class)
@Import(GlobalExceptionHandler.class)
class DocumentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private DocumentService documentService;

    @Test
    void shouldReturnAllDocuments() throws Exception {

        DocumentResponse response = createDocumentResponse(1L);

        when(documentService.findAll())
                .thenReturn(List.of(response));

        mockMvc.perform(get("/api/documents"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("Contrato"))
                .andExpect(jsonPath("$[0].processingStatus").value("PENDING"));
    }

    @Test
    void shouldReturnDocumentById() throws Exception {

        DocumentResponse response = createDocumentResponse(1L);

        when(documentService.findById(1L))
                .thenReturn(response);

        mockMvc.perform(get("/api/documents/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Contrato"))
                .andExpect(jsonPath("$.fileName").value("contrato.pdf"))
                .andExpect(jsonPath("$.fileSize").value(1024));
    }

    @Test
    void shouldCreateDocument() throws Exception {

        CreateDocumentRequest request = new CreateDocumentRequest(
                "Novo documento",
                "Documento criado no teste",
                "novo-documento.pdf",
                "application/pdf",
                2048L
        );

        DocumentResponse response = new DocumentResponse(
                2L,
                request.title(),
                request.description(),
                request.fileName(),
                request.contentType(),
                request.fileSize(),
                LocalDateTime.now(),
                LocalDateTime.now(),
                ProcessingStatus.PENDING,
                null
        );

        when(documentService.create(any(CreateDocumentRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/documents")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.title").value("Novo documento"))
                .andExpect(jsonPath("$.fileName").value("novo-documento.pdf"))
                .andExpect(jsonPath("$.processingStatus").value("PENDING"));
    }

    @Test
    void shouldUpdateDocument() throws Exception {

        UpdateDocumentRequest request = new UpdateDocumentRequest(
                "Documento atualizado",
                "Documento atualizado no teste",
                "documento-atualizado.pdf",
                "application/pdf",
                4096L
        );

        DocumentResponse response = new DocumentResponse(
                1L,
                request.title(),
                request.description(),
                request.fileName(),
                request.contentType(),
                request.fileSize(),
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now(),
                ProcessingStatus.PENDING,
                null
        );

        when(documentService.update(
                eq(1L),
                any(UpdateDocumentRequest.class)
        )).thenReturn(response);

        mockMvc.perform(put("/api/documents/1")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Documento atualizado"))
                .andExpect(jsonPath("$.fileName").value("documento-atualizado.pdf"))
                .andExpect(jsonPath("$.fileSize").value(4096));
    }

    @Test
    void shouldReturnNotFoundWhenDocumentDoesNotExist() throws Exception {

        when(documentService.findById(999L))
                .thenThrow(new ResourceNotFoundException(
                        "Documento não encontrado: 999"
                ));

        mockMvc.perform(get("/api/documents/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error")
                        .value("Documento não encontrado: 999"));
    }

    @Test
    void shouldReturnBadRequestWhenCreateRequestIsInvalid() throws Exception {

        CreateDocumentRequest request = new CreateDocumentRequest(
                "",
                "Documento inválido",
                "teste.pdf",
                "application/pdf",
                -500L
        );

        mockMvc.perform(post("/api/documents")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Dados inválidos"))
                .andExpect(jsonPath("$.fields.title").exists())
                .andExpect(jsonPath("$.fields.fileSize").exists());
    }

    private DocumentResponse createDocumentResponse(Long id) {

        LocalDateTime now = LocalDateTime.now();

        return new DocumentResponse(
                id,
                "Contrato",
                "Contrato de teste",
                "contrato.pdf",
                "application/pdf",
                1024L,
                now.minusDays(1),
                now,
                ProcessingStatus.PENDING,
                null
        );
    }
}
