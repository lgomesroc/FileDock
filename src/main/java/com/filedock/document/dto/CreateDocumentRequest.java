package com.filedock.document.dto;

public record CreateDocumentRequest(
        String title,
        String description,
        String fileName,
        String contentType,
        Long fileSize
) {
}
