package com.filedock.storage;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;

public interface FileStorageService {

    String store(MultipartFile file) throws IOException;

    Path load(String storagePath) throws IOException;

    void delete(String storagePath) throws IOException;
}
