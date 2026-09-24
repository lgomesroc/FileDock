package com.filedock.document.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record CreateDocumentRequest(

        @Schema(
                description = "Título do documento",
                example = "Contrato de prestação de serviços"
        )
        @NotBlank
        @Size(max = 200)
        String title,

        @Schema(
                description = "Descrição do documento",
                example = "Contrato referente à prestação de serviços de TI"
        )
        @Size(max = 1000)
        String description,

        @Schema(
                description = "Nome original do arquivo",
                example = "contrato-ti.pdf"
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
                example = "2048",
                minimum = "1"
        )
        @NotNull
        @Positive
        Long fileSize
) {
}
