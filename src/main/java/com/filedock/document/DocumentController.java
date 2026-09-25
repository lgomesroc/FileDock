package com.filedock.document;

import com.filedock.document.dto.CreateDocumentRequest;
import com.filedock.document.dto.DocumentDownload;
import com.filedock.document.dto.DocumentResponse;
import com.filedock.document.dto.UpdateDocumentRequest;
import com.filedock.exception.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/documents")
@Tag(
        name = "Documents",
        description = "Operações de gerenciamento de documentos"
)
public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @Operation(
            summary = "Lista todos os documentos",
            description = "Retorna todos os documentos cadastrados."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Documentos retornados com sucesso."
    )
    @GetMapping
    public List<DocumentResponse> findAll() {
        return documentService.findAll();
    }

    @Operation(
            summary = "Busca um documento",
            description = "Retorna um documento pelo seu ID."
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
            summary = "Baixa o arquivo de um documento",
            description = "Retorna o arquivo físico associado ao documento."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Arquivo retornado com sucesso."
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
    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> download(
            @Parameter(
                    description = "ID do documento",
                    example = "4"
            )
            @PathVariable Long id) {

        DocumentDownload download =
                documentService.download(id);

        return ResponseEntity.ok()
                .contentType(
                        MediaType.parseMediaType(
                                download.contentType()
                        )
                )
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\""
                                + download.fileName()
                                + "\""
                )
                .body(download.resource());
    }

    @Operation(
            summary = "Cria um documento",
            description = "Cria um documento utilizando seus metadados."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Documento criado com sucesso."
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dados inválidos.",
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
            @Valid @org.springframework.web.bind.annotation.RequestBody
            CreateDocumentRequest request) {

        return documentService.create(request);
    }

    @Operation(
            summary = "Cria um documento com upload de arquivo",
            description = "Armazena o arquivo e cria o documento."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Documento criado com sucesso."
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dados inválidos ou arquivo ausente.",
                    content = @Content(
                            schema = @Schema(
                                    implementation = ErrorResponse.class
                            )
                    )
            )
    })
    @PostMapping(
            value = "/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @ResponseStatus(HttpStatus.CREATED)
    public DocumentResponse upload(
            @RequestParam String title,
            @RequestParam(required = false) String description,
            @RequestPart("file") MultipartFile file) {

        return documentService.upload(
                title,
                description,
                file
        );
    }

    @Operation(
            summary = "Atualiza os dados de um documento",
            description = "Atualiza os metadados do documento."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Documento atualizado com sucesso."
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dados inválidos.",
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
                    example = "4"
            )
            @PathVariable Long id,
            @Valid @org.springframework.web.bind.annotation.RequestBody
            UpdateDocumentRequest request) {

        return documentService.update(id, request);
    }

    @Operation(
            summary = "Substitui o arquivo de um documento",
            description = "Substitui o arquivo físico mantendo o documento e seus metadados principais."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Arquivo substituído com sucesso."
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Arquivo ausente ou inválido.",
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
    @PutMapping(
            value = "/{id}/file",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public DocumentResponse replaceFile(
            @Parameter(
                    description = "ID do documento",
                    example = "4"
            )
            @PathVariable Long id,
            @RequestPart("file") MultipartFile file) {

        return documentService.replaceFile(id, file);
    }

    @Operation(
            summary = "Exclui um documento",
            description = "Exclui o documento e o arquivo físico associado."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Documento excluído com sucesso."
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
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @Parameter(
                    description = "ID do documento",
                    example = "4"
            )
            @PathVariable Long id) {

        documentService.delete(id);
    }
}
