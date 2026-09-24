package com.filedock.document;

import com.filedock.document.dto.CreateDocumentRequest;
import com.filedock.document.dto.DocumentResponse;
import com.filedock.document.dto.UpdateDocumentRequest;
import com.filedock.exception.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @Operation(
            summary = "Lista todos os documentos",
            description = "Retorna todos os documentos cadastrados no sistema."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Documentos encontrados com sucesso."
    )
    @GetMapping
    public List<DocumentResponse> findAll() {
        return documentService.findAll();
    }

    @Operation(
            summary = "Busca um documento por ID",
            description = "Retorna os dados de um documento específico."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Documento encontrado."
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Documento não encontrado.",
                    content = @Content(
                            schema = @Schema(
                                    implementation = ErrorResponse.class
                            )
                    )
            )
    })
    @GetMapping("/{id}")
    public DocumentResponse findById(
            @Parameter(
                    description = "ID do documento",
                    example = "1"
            )
            @PathVariable Long id) {

        return documentService.findById(id);
    }

    @Operation(
            summary = "Cadastra um documento",
            description = "Cria um novo documento com status inicial PENDING."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Documento criado com sucesso."
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dados de entrada inválidos.",
                    content = @Content(
                            schema = @Schema(
                                    implementation = ErrorResponse.class
                            )
                    )
            )
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DocumentResponse create(
            @Valid @RequestBody CreateDocumentRequest request) {

        return documentService.create(request);
    }

    @Operation(
            summary = "Atualiza um documento",
            description = "Atualiza os dados de um documento existente."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Documento atualizado com sucesso."
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dados de entrada inválidos.",
                    content = @Content(
                            schema = @Schema(
                                    implementation = ErrorResponse.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Documento não encontrado.",
                    content = @Content(
                            schema = @Schema(
                                    implementation = ErrorResponse.class
                            )
                    )
            )
    })
    @PutMapping("/{id}")
    public DocumentResponse update(
            @Parameter(
                    description = "ID do documento",
                    example = "1"
            )
            @PathVariable Long id,
            @Valid @RequestBody UpdateDocumentRequest request) {

        return documentService.update(id, request);
    }
}
