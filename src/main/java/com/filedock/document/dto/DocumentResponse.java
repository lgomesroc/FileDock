package com.filedock.document.dto;

import com.filedock.document.ProcessingStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Dados de um documento armazenado no FileDock")
public record DocumentResponse(

        @Schema(
                description = "Identificador único do documento",
                example = "1"
        )
        Long id,

        @Schema(
                description = "Título do documento",
                example = "Contrato de prestação de serviços"
        )
        String title,

        @Schema(
                description = "Descrição do documento",
                example = "Contrato referente à prestação de serviços de TI"
        )
        String description,

        @Schema(
                description = "Nome do arquivo",
                example = "contrato-ti.pdf"
        )
        String fileName,

        @Schema(
                description = "Tipo MIME do arquivo",
                example = "application/pdf"
        )
        String contentType,

        @Schema(
                description = "Tamanho do arquivo em bytes",
                example = "2048"
        )
        Long fileSize,

        @Schema(
                description = "Data e hora de criação do documento",
                example = "2026-09-24T14:30:00"
        )
        LocalDateTime createdAt,

        @Schema(
                description = "Data e hora da última atualização",
                example = "2026-09-24T15:45:00"
        )
        LocalDateTime updatedAt,

        @Schema(
                description = "Status atual do processamento do documento",
                example = "PENDING"
        )
        ProcessingStatus processingStatus,

        @Schema(
                description = "Data e hora em que o processamento foi concluído",
                example = "2026-09-24T16:00:00"
        )
        LocalDateTime processedAt
) {
}
