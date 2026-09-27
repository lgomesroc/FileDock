package com.filedock.document.dto;

import com.filedock.document.ProcessingStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Dados de um documento")
public record DocumentResponse(

        @Schema(description = "Identificador do documento", example = "1")
        Long id,

        @Schema(description = "Título do documento",
                example = "Contrato de prestação de serviços")
        String title,

        @Schema(description = "Descrição do documento",
                example = "Contrato referente à prestação de serviços de TI")
        String description,

        @Schema(description = "Nome original do arquivo",
                example = "contrato-ti.pdf")
        String fileName,

        @Schema(description = "Tipo MIME do arquivo",
                example = "application/pdf")
        String contentType,

        @Schema(description = "Tamanho do arquivo em bytes",
                example = "2048")
        Long fileSize,

        @Schema(description = "Caminho do arquivo armazenado",
                example = "550e8400-e29b-41d4-a716-446655440000.pdf")
        String storagePath,

        @Schema(description = "Data de criação do documento",
                example = "2026-09-24T10:30:00")
        LocalDateTime createdAt,

        @Schema(description = "Data da última atualização",
                example = "2026-09-24T10:35:00")
        LocalDateTime updatedAt,

        @Schema(description = "Status de processamento do documento",
                example = "PENDING",
                allowableValues = {"PENDING", "PROCESSED"})
        ProcessingStatus processingStatus,

        @Schema(description = "Data em que o processamento foi concluído",
                example = "2026-09-24T10:40:00")
        LocalDateTime processedAt
) {
}
