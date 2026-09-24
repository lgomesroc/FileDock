package com.filedock.document.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record UpdateDocumentRequest(

        @Schema(
                description = "Título do documento",
                example = "Contrato atualizado"
        )
        @NotBlank
        @Size(max = 200)
        String title,

        @Schema(
                description = "Descrição do documento",
                example = "Descrição atualizada do contrato"
        )
        @Size(max = 1000)
        String description,

        @Schema(
                description = "Nome do arquivo",
                example = "contrato-atualizado.pdf"
        )
        @NotBlank
        @Size(max = 255)
        String fileName,

        @Schema(
                description = "Tipo MIME do arquivo",
                example = "application/pdf"
        )
        @NotBlank
        @Size(max = 100)
        String contentType,

        @Schema(
                description = "Tamanho do arquivo em bytes",
                example = "4096",
                minimum = "1"
        )
        @NotNull
        @Positive
        Long fileSize
) {
}
