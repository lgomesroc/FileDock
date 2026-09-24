package com.filedock.exception;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Map;

@Schema(description = "Resposta de erro da API")
public record ErrorResponse(

        @Schema(
                description = "Mensagem principal do erro",
                example = "Dados inválidos"
        )
        String error,

        @Schema(
                description = "Erros encontrados nos campos da requisição",
                example = "{\"title\":\"must not be blank\",\"fileSize\":\"must be greater than 0\"}"
        )
        Map<String, String> fields
) {
}
