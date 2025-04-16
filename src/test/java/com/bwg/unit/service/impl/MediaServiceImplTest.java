package com.bwg.unit.service.impl;

import com.bwg.domain.Media;
import com.bwg.exception.ResourceNotFoundException;
import com.bwg.model.MediaModel;
import com.bwg.repository.MediaRepository;
import com.bwg.service.StorageService;
import com.bwg.service.impl.MediaServiceImpl;
import com.bwg.util.BeanUtil;
import com.querydsl.core.BooleanBuilder;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class MediaServiceImplTest {
    @Mock
    private MediaRepository mediaRepository;

    @Mock
    private StorageService storageService;
    @InjectMocks
    private MediaServiceImpl mediaService;


    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllMedia_withFilters() {
        Pageable pageable = PageRequest.of(0, 10);

        Media media = new Media();
        media.setTitle("Wedding Photos");
        media.setEntityId(101L);

        Page<Media> mediaPage = new PageImpl<>(List.of(media));

        when(mediaRepository.findAll(any(BooleanBuilder.class), eq(pageable)))
                .thenReturn(mediaPage);

        Page<Media> result = mediaService.getAllMedia("wedding", 101L, pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("Wedding Photos", result.getContent().get(0).getTitle());
        assertEquals(101L, result.getContent().get(0).getEntityId());
    }

    @Test
    void testGetAllMedia_withNullFilters() {
        Pageable pageable = PageRequest.of(0, 5);

        Media media = new Media();
        media.setTitle("Venue Tour");
        Page<Media> mediaPage = new PageImpl<>(List.of(media));

        when(mediaRepository.findAll(any(BooleanBuilder.class), eq(pageable)))
                .thenReturn(mediaPage);

        Page<Media> result = mediaService.getAllMedia(null, null, pageable);
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("Venue Tour", result.getContent().get(0).getTitle());
    }

    @Test
    void testGetByEntity_returnsMediaList() {
        // Arrange
        Long entityId = 101L;

        Media media1 = new Media();
        media1.setMediaId(1L);
        media1.setTitle("Intro Video");
        media1.setEntityId(entityId);
        media1.setMediaUrl("media1.mp4");
        media1.setThumbnailUrl("thumb1.jpg");

        List<Media> mockMediaList = List.of(media1);

        StorageService mockStorageService = mock(StorageService.class);
        when(mockStorageService.getUrl("media1.mp4")).thenReturn("https://cdn.com/media1.mp4");
        when(mockStorageService.getUrl("thumb1.jpg")).thenReturn("https://cdn.com/thumb1.jpg");
        try (MockedStatic<BeanUtil> mockedStatic = mockStatic(BeanUtil.class)) {
            mockedStatic.when(() -> BeanUtil.getBean(StorageService.class))
                    .thenReturn(mockStorageService);

            when(mediaRepository.findAllByEntityId(entityId)).thenReturn(mockMediaList);

            List<MediaModel> result = mediaService.getByEntity(entityId);

            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals("Intro Video", result.get(0).getTitle());
            assertEquals("https://cdn.com/media1.mp4", result.get(0).getPublicUrl());
            assertEquals("https://cdn.com/thumb1.jpg", result.get(0).getPublicThumbnailUrl());

            // 👉 Optional verify
            mockedStatic.verify(() -> BeanUtil.getBean(StorageService.class), times(2));
        }
    }



    @Test
    void testGetByEntity_returnsEmptyList() {
        Long entityId = 202L;
        when(mediaRepository.findAllByEntityId(entityId)).thenReturn(List.of());
        List<MediaModel> result = mediaService.getByEntity(entityId);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetMedia_returnsMediaIfFound() {
        // Arrange
        Long mediaId = 1L;

        Media media = new Media();
        media.setMediaId(mediaId);
        media.setTitle("Gallery Photo");

        when(mediaRepository.findById(mediaId)).thenReturn(Optional.of(media));
        Media result = mediaService.getMedia(mediaId);
        assertNotNull(result);
        assertEquals(mediaId, result.getMediaId());
        assertEquals("Gallery Photo", result.getTitle());
    }


    @Test
    void testGetMedia_throwsIfNotFound() {
        Long mediaId = 404L;
        when(mediaRepository.findById(mediaId)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> mediaService.getMedia(mediaId));

        verify(mediaRepository, times(1)).findById(mediaId);
    }

    @Test
    void testUploadMedia_successfullyUploadsAndSaves() throws IOException {
        Long entityId = 1L;
        String title = "Test Media";
        Media.EntityType entityType = Media.EntityType.service;

        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.getContentType()).thenReturn("image/jpeg");

        String fileUrl = "https://cdn.com/media/test.jpg";
        String thumbnailUrl = "https://cdn.com/media/thumb_test.jpg";

        ByteArrayInputStream thumbnailStream = new ByteArrayInputStream(new byte[]{1, 2, 3});
        String thumbnailFileName = "thumb_test.jpg";
        MediaServiceImpl mediaServiceSpy = spy(new MediaServiceImpl(storageService,mediaRepository));

        doReturn(Pair.of(thumbnailStream, thumbnailFileName))
                .when(mediaServiceSpy)
                .generateThumbnailStream(any(MultipartFile.class));

        when(storageService.uploadFile(mockFile)).thenReturn(fileUrl);
        when(storageService.uploadFile(thumbnailStream, thumbnailFileName, "image/jpeg")).thenReturn(thumbnailUrl);

        Media savedMedia = new Media();
        savedMedia.setMediaId(101L);
        when(mediaRepository.save(any(Media.class))).thenAnswer(invocation -> {
            Media m = invocation.getArgument(0);
            m.setMediaId(101L);
            return m;
        });

        Media result = mediaServiceSpy.uploadMedia(entityId, title, entityType, mockFile);

        assertNotNull(result);
        assertEquals("/media/101/download", result.getMediaUri());
        assertEquals(fileUrl, result.getMediaUrl());
        assertEquals(thumbnailUrl, result.getThumbnailUrl());

        verify(storageService).uploadFile(mockFile);
        verify(storageService).uploadFile(thumbnailStream, thumbnailFileName, "image/jpeg");
        verify(mediaRepository, times(2)).save(any());
    }

    @Test
    void testDownloadFile_successfullyReturnsData() {
        Long mediaId = 1L;

        Media media = new Media();
        media.setMediaId(mediaId);
        media.setMediaUrl("test-file.jpg");

        byte[] fileContent = "Hello World!".getBytes();
        ResponseEntity<byte[]> expectedResponse = ResponseEntity
                .ok()
                .header("Content-Disposition", "attachment; filename=test-file.jpg")
                .body(fileContent);

        when(mediaRepository.findById(mediaId)).thenReturn(Optional.of(media));
        when(storageService.downloadFile("test-file.jpg")).thenReturn(expectedResponse);

        ResponseEntity<byte[]> result = mediaService.downloadFile(mediaId);
        assertNotNull(result);
        assertArrayEquals(fileContent, result.getBody());
        assertEquals(200, result.getStatusCodeValue());
        assertTrue(result.getHeaders().getFirst("Content-Disposition").contains("test-file.jpg"));

        verify(storageService).downloadFile("test-file.jpg");
        verify(mediaRepository).findById(mediaId);
    }

    @Test
    void testDownloadFile_throwsIfMediaNotFound() {
        Long mediaId = 404L;
        when(mediaRepository.findById(mediaId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> mediaService.downloadFile(mediaId));

        assertEquals("Media not found with ID: 404", exception.getMessage());
        verify(mediaRepository).findById(mediaId);
        verify(storageService, never()).downloadFile(anyString());
    }

    @Test
    void testDeleteMedia_successfullyDeletes() {
        Long mediaId = 100L;
        Media media = new Media();
        media.setMediaId(mediaId);
        when(mediaRepository.findById(mediaId)).thenReturn(Optional.of(media));

        mediaService.deleteMedia(mediaId);

        verify(mediaRepository).findById(mediaId);
        verify(mediaRepository).delete(media);
    }

    @Test
    void testDeleteMedia_throwsIfNotFound() {
        Long mediaId = 404L;
        when(mediaRepository.findById(mediaId)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> {
            mediaService.deleteMedia(mediaId);
        });

        verify(mediaRepository).findById(mediaId);
        verify(mediaRepository, never()).delete(any());
    }


}
