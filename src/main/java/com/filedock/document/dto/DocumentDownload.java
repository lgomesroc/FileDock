package com.filedock.document.dto;

import org.springframework.core.io.Resource;

public record DocumentDownload(
        Resource resource,
        String fileName,
        String contentType
) {}
