package com.bwg.service.impl;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.bwg.service.StorageService;
import com.bwg.util.S3Util;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

@Service("s3StorageService")
@ConditionalOnProperty(name = "storage.type", havingValue = "S3")
public class S3StorageServiceImpl implements StorageService {
    private final AmazonS3 s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;
    @Value("${s3.folder}")
    private String s3Folder;

    public S3StorageServiceImpl(AmazonS3 s3Client) {
        this.s3Client = s3Client;
    }

    public String uploadFile(MultipartFile file) throws IOException {
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType());
        String s3Key = s3Folder + fileName;

        s3Client.putObject(bucketName, s3Key, file.getInputStream(), metadata);

        return s3Client.getUrl(bucketName, s3Key).toString();
    }

    @Override
    public ResponseEntity<byte[]> downloadFile(String fileName) {
        fileName = S3Util.extractFileKey(fileName);
        String s3Key = s3Folder + fileName;
        try {
            S3Object s3Object = s3Client.getObject(bucketName, s3Key);
            S3ObjectInputStream inputStream = s3Object.getObjectContent();
            byte[] content = inputStream.readAllBytes();

            String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8);

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedFileName + "\"");
            headers.add(HttpHeaders.CONTENT_TYPE, s3Object.getObjectMetadata().getContentType());
            headers.add(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate");
            headers.add(HttpHeaders.PRAGMA, "no-cache");
            headers.add(HttpHeaders.EXPIRES, "0");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(content);

        } catch (AmazonS3Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(("File not found in S3: " + fileName).getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Error reading file from S3: " + fileName).getBytes(StandardCharsets.UTF_8));
        }
    }

    @Override
    public String getUrl(String fileName) {
        fileName = S3Util.extractFileKey(fileName);
        String s3Key = s3Folder + fileName;
        try {

            Date expiration = new Date(System.currentTimeMillis() + 1000 * 60 * 10);

            GeneratePresignedUrlRequest generatePresignedUrlRequest =
                    new GeneratePresignedUrlRequest(bucketName, s3Key)
                            .withMethod(HttpMethod.GET)
                            .withExpiration(expiration);

            return s3Client.generatePresignedUrl(generatePresignedUrlRequest).toString();
        } catch (Exception e) {
            throw new RuntimeException("Could not generate  URL for file: " + fileName, e);
        }
    }
}
