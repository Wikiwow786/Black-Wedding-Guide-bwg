package com.bwg.service;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface StorageService {
    String uploadFile(MultipartFile file) throws IOException;
    ResponseEntity<byte[]> downloadFile(String fileName);
    String getUrl(String fileName);
}
