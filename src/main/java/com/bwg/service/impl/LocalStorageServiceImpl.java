package com.bwg.service.impl;

import com.bwg.service.StorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.UUID;

@Service("localStorageService")
@ConditionalOnProperty(name = "storage.type", havingValue = "local",matchIfMissing = true)
public class LocalStorageServiceImpl implements StorageService {
    @Value("${media.storage.local-path}")
    private String localStoragePath;

    @Override
    public String uploadFile(MultipartFile file) throws IOException {
        File directory = new File(localStoragePath);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        File destinationFile = new File(localStoragePath + File.separator + fileName);
        Files.copy(file.getInputStream(), destinationFile.toPath());
        return destinationFile.getAbsolutePath();
    }

    @Override
    public ResponseEntity<byte[]> downloadFile(String fileName) {
        return null;
    }

    @Override
    public String getUrl(String fileName) {
        return "";
    }

    @Override
    public String uploadFile(InputStream inputStream, String fileName, String contentType) throws IOException {
        return "";
    }
}
