package com.bwg.unit.service.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.bwg.service.impl.S3StorageServiceImpl;
import com.bwg.util.S3Util;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
public class S3StorageServiceImplTest {


        @Mock
        private AmazonS3 s3Client;

        private S3StorageServiceImpl storageService;

        @BeforeEach
        void setup() throws Exception {
            storageService = new S3StorageServiceImpl(s3Client);

            Field bucketField = S3StorageServiceImpl.class.getDeclaredField("bucketName");
            bucketField.setAccessible(true);
            bucketField.set(storageService, "my-bucket");

            Field folderField = S3StorageServiceImpl.class.getDeclaredField("s3Folder");
            folderField.setAccessible(true);
            folderField.set(storageService, "media/");
        }

        @Test
        void testUploadFile_successfullyReturnsUrl() throws Exception {
            MultipartFile file = mock(MultipartFile.class);
            when(file.getOriginalFilename()).thenReturn("test-image.png");
            when(file.getContentType()).thenReturn("image/png");
            when(file.getInputStream()).thenReturn(new ByteArrayInputStream("dummy".getBytes()));

            when(s3Client.putObject(anyString(), anyString(), any(InputStream.class), any(ObjectMetadata.class)))
                    .thenReturn(new PutObjectResult());

            when(s3Client.getUrl(eq("my-bucket"), anyString()))
                    .thenReturn(new URL("https://dummy-bucket.s3.amazonaws.com/test.png"));

            String result = storageService.uploadFile(file);

            assertNotNull(result);
            assertTrue(result.endsWith(".png"));

            verify(s3Client).putObject(eq("my-bucket"), anyString(), any(), any());
            verify(s3Client).getUrl(eq("my-bucket"), anyString());
        }

        @Test
        void testDownloadFile_successfullyReturnsFile() {
            String fileName = "test.png";
            String extractedKey = "test.png";
            byte[] fileContent = "fake file content".getBytes(StandardCharsets.UTF_8);
            String contentType = "image/png";

            S3Object s3Object = mock(S3Object.class);
            S3ObjectInputStream inputStream = new S3ObjectInputStream(
                    new ByteArrayInputStream(fileContent), null);

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(contentType);

            when(s3Client.getObject("my-bucket", "media/" + extractedKey)).thenReturn(s3Object);
            when(s3Object.getObjectContent()).thenReturn(inputStream);
            when(s3Object.getObjectMetadata()).thenReturn(metadata);

            try (MockedStatic<S3Util> mockedStatic = mockStatic(S3Util.class)) {
                mockedStatic.when(() -> S3Util.extractFileKey(fileName)).thenReturn(extractedKey);

                ResponseEntity<byte[]> response = storageService.downloadFile(fileName);

                assertEquals(HttpStatus.OK, response.getStatusCode());
                assertArrayEquals(fileContent, response.getBody());
                assertEquals("attachment; filename=\"test.png\"",
                        response.getHeaders().getFirst(HttpHeaders.CONTENT_DISPOSITION));
                assertEquals("image/png", response.getHeaders().getFirst(HttpHeaders.CONTENT_TYPE));
            }
        }

    @Test
    void testGetUrl_successfullyReturnsPreSignedUrl() throws Exception {
        String fileName = "image.jpg";
        String extractedKey = "image.jpg";
        String expectedUrl = "https://signed-url.s3.amazonaws.com/media/image.jpg";

        URL mockedUrl = new URL(expectedUrl);

        when(s3Client.generatePresignedUrl(any(GeneratePresignedUrlRequest.class)))
                .thenReturn(mockedUrl);

        try (MockedStatic<S3Util> mockedStatic = mockStatic(S3Util.class)) {
            mockedStatic.when(() -> S3Util.extractFileKey(fileName)).thenReturn(extractedKey);

            String result = storageService.getUrl(fileName);

            assertEquals(expectedUrl, result);
            verify(s3Client).generatePresignedUrl(any(GeneratePresignedUrlRequest.class));
        }
    }

    @Test
    void testGetUrl_throwsRuntimeExceptionIfFails() {
        String fileName = "broken.jpg";
        String extractedKey = "broken.jpg";

        when(s3Client.generatePresignedUrl(any(GeneratePresignedUrlRequest.class)))
                .thenThrow(new RuntimeException("S3 failure"));

        try (MockedStatic<S3Util> mockedStatic = mockStatic(S3Util.class)) {
            mockedStatic.when(() -> S3Util.extractFileKey(fileName)).thenReturn(extractedKey);

            RuntimeException exception = assertThrows(RuntimeException.class, () -> storageService.getUrl(fileName));

            assertTrue(exception.getMessage().contains("Could not generate"));
        }
    }
    @Test
    void testUploadFileWithStream_successfullyUploads() throws Exception {
        String fileName = "uploaded.png";
        String contentType = "image/png";
        byte[] fileData = "fake image data".getBytes(StandardCharsets.UTF_8);
        InputStream inputStream = new ByteArrayInputStream(fileData);

        when(s3Client.putObject(anyString(), anyString(), any(InputStream.class), any(ObjectMetadata.class)))
                .thenReturn(new PutObjectResult());

        when(s3Client.getUrl(eq("my-bucket"), eq("media/" + fileName)))
                .thenReturn(new URL("https://dummy-bucket.s3.amazonaws.com/media/uploaded.png"));

        String result = storageService.uploadFile(inputStream, fileName, contentType);

        assertNotNull(result);
        assertTrue(result.endsWith(".png"));

        verify(s3Client).putObject(eq("my-bucket"), eq("media/" + fileName), any(), any());
        verify(s3Client).getUrl(eq("my-bucket"), eq("media/" + fileName));
    }


}


