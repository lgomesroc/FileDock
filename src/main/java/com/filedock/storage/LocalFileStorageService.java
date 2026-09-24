package com.filedock.storage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class LocalFileStorageService implements FileStorageService {

    private final Path storageLocation;

    public LocalFileStorageService(
            @Value("${file.storage.location:uploads}") String storageLocation) {

        this.storageLocation = Paths.get(storageLocation)
                .toAbsolutePath()
                .normalize();
    }

    @Override
    public String store(MultipartFile file) throws IOException {

        Files.createDirectories(storageLocation);

        String extension = extractExtension(file.getOriginalFilename());

        String storedFileName = UUID.randomUUID() + extension;

        Path targetLocation = storageLocation.resolve(storedFileName)
                .normalize();

        if (!targetLocation.getParent().equals(storageLocation)) {
            throw new IOException("Caminho de armazenamento inválido.");
        }

        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, targetLocation);
        }

        return storageLocation.relativize(targetLocation).toString();
    }

    private String extractExtension(String fileName) {

        if (fileName == null || fileName.isBlank()) {
            return "";
        }

        int lastDot = fileName.lastIndexOf('.');

        if (lastDot < 0) {
            return "";
        }

        return fileName.substring(lastDot);
    }
}
