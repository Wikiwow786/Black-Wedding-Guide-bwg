package com.bwg.unit.restapi;

import com.bwg.config.MethodSecurityConfig;
import com.bwg.domain.Media;
import com.bwg.exception.ResourceNotFoundException;
import com.bwg.model.MediaModel;
import com.bwg.restapi.MediaController;
import com.bwg.service.MediaService;
import com.bwg.unit.config.TestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;

@WebMvcTest(controllers = MediaController.class)
@Import({TestConfig.class, MethodSecurityConfig.class})
class MediaControllerTest extends BaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MediaService mediaService;

    @Test
    @WithMockUser(authorities = {"ROLE_ADMIN"})
    void getMediaById_ShouldReturn200_WhenExists() throws Exception {
        Media media = new Media();
        media.setMediaId(1L);
        media.setTitle("main image");
        media.setEntityId(123L);
        media.setCreatedAt(OffsetDateTime.now());

        when(mediaService.getMedia(1L)).thenReturn(media);

        mockMvc.perform(get("/media/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                //.andExpect(jsonPath("$.media_id").value(1L))
                .andExpect(jsonPath("$.title").value("main image"))
                .andDo(print());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ROLE_COUPLE"})
    void getMediaById_ShouldReturn403_WhenUnauthorizedRole() throws Exception {
        mockMvc.perform(get("/media/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = {"ROLE_ADMIN"})
    void getMediaById_ShouldReturn404_WhenMediaNotFound() throws Exception {
        when(mediaService.getMedia(99L)).thenThrow(new com.bwg.exception.ResourceNotFoundException("Media not found"));

        mockMvc.perform(get("/media/99")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Media not found"))
                .andDo(print());
    }

    @Test
    @WithMockUser(username = "user", authorities = {"ROLE_COUPLE"})
    void downloadFile_ShouldReturn200() throws Exception {
        byte[] fakeFile = "test file content".getBytes(StandardCharsets.UTF_8);

        doReturn(ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"file.txt\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(fakeFile))
                .when(mediaService).downloadFile(eq(1L));

        mockMvc.perform(get("/media/1/download")
                        .accept(MediaType.APPLICATION_OCTET_STREAM))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"file.txt\""))
                .andExpect(content().bytes(fakeFile))
                .andDo(print());
    }

    @Test
    void downloadFile_ShouldReturn401_WhenUnauthenticated() throws Exception {
        mockMvc.perform(get("/media/1/download"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "user", authorities = {"ROLE_OWNER"})
    void downloadFile_ShouldReturn404_WhenNotFound() throws Exception {
        doThrow(new ResourceNotFoundException("Media not found"))
                .when(mediaService).downloadFile(eq(999L));

        mockMvc.perform(get("/media/999/download"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Media not found"))
                .andDo(print());
    }

    @Test
    @WithMockUser
    void getEntity_ShouldReturnListOfMedia() throws Exception {
        Long entityId = 42L;

        MediaModel media = new MediaModel();
        media.setMediaId(1L);
        media.setEntityId(entityId);
        media.setEntityType(Media.EntityType.service);
        media.setMediaUrl("media/image.jpg");
        media.setMediaUri("s3://bucket/media/image.jpg");
        media.setThumbnailUrl("media/image_thumb.jpg");
        media.setPublicUrl("https://cdn.example.com/media/image.jpg");
        media.setPublicThumbnailUrl("https://cdn.example.com/media/image_thumb.jpg");
        media.setMimeType("image/jpeg");
        media.setTitle("Wedding Image");
        media.setCreatedAt(OffsetDateTime.now().withNano(0));

        List<MediaModel> mediaList = List.of(media);

        doReturn(mediaList).when(mediaService).getByEntity(eq(entityId));

        mockMvc.perform(get("/media/entity/{entityId}", entityId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].media_id").value(1))
                .andExpect(jsonPath("$[0].entity_id").value(42))
                .andExpect(jsonPath("$[0].media_url").value("media/image.jpg"))
                .andExpect(jsonPath("$[0].public_url").value("https://cdn.example.com/media/image.jpg"))
                .andExpect(jsonPath("$[0].thumbnail_url").value("media/image_thumb.jpg"))
                .andExpect(jsonPath("$[0].public_thumbnail_url").value("https://cdn.example.com/media/image_thumb.jpg"))
                .andExpect(jsonPath("$[0].mime_type").value("image/jpeg"))
                .andExpect(jsonPath("$[0].title").value("Wedding Image"))
                .andExpect(jsonPath("$[0].created_at").exists())
                .andDo(print());
    }

    @Test
    @WithMockUser
    void getEntity_ShouldReturnEmptyList_WhenNoMediaExists() throws Exception {
        Long entityId = 99L;

        doReturn(Collections.emptyList()).when(mediaService).getByEntity(eq(entityId));

        mockMvc.perform(get("/media/entity/{entityId}", entityId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0))
                .andDo(print());
    }

    @Test
    @WithMockUser
    void getUrl_ShouldReturnPublicUrl() throws Exception {
        Long mediaId = 1L;
        String expectedUrl = "https://cdn.example.com/media/file.jpg";

        doReturn(expectedUrl).when(mediaService).getUrl(mediaId);

        mockMvc.perform(get("/media/url/{mediaId}", mediaId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedUrl))
                .andDo(print());
    }

    @Test
    @WithMockUser(authorities = {"ROLE_ADMIN"})
    void uploadMedia_ShouldReturn201_WhenSuccessful() throws Exception {
        MockMultipartFile mockFile = new MockMultipartFile(
                "file", "image.jpg", MediaType.IMAGE_JPEG_VALUE, "test image content".getBytes()
        );

        Media media = new Media();
        media.setMediaId(1L);
        media.setTitle("Sample Image");

        doReturn(media).when(mediaService)
                .uploadMedia(eq(123L), eq("Sample Image"), eq(Media.EntityType.service), any(MultipartFile.class));

        mockMvc.perform(multipart("/media")
                        .file(mockFile)
                        .param("entityId", "123")
                        .param("title", "Sample Image")
                        .param("entityType", "service")
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Sample Image"))
                .andDo(print());
    }
    @Test
    @WithMockUser(authorities = {"ROLE_COUPLE"})
    void uploadMedia_ShouldReturn403_WhenUnauthorized() throws Exception {
        MockMultipartFile mockFile = new MockMultipartFile(
                "file", "image.jpg", MediaType.IMAGE_JPEG_VALUE, "test image content".getBytes()
        );

        mockMvc.perform(multipart("/media")
                        .file(mockFile)
                        .param("entityId", "123")
                        .param("title", "Sample Image")
                        .param("entityType", "service")
                        .with(csrf()))
                .andExpect(status().isForbidden())
                .andDo(print());
    }

    @Test
    @WithMockUser(authorities = {"ROLE_ADMIN"})
    void uploadMedia_ShouldReturn400_WhenIOExceptionOccurs() throws Exception {
        MockMultipartFile mockFile = new MockMultipartFile(
                "file", "image.jpg", MediaType.IMAGE_JPEG_VALUE, "test image content".getBytes()
        );

        doThrow(new IOException("Disk full")).when(mediaService)
                .uploadMedia(any(), any(), any(), any(MultipartFile.class));

        mockMvc.perform(multipart("/media")
                        .file(mockFile)
                        .param("entityId", "123")
                        .param("title", "Sample Image")
                        .param("entityType", "service")
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("File upload failed")))
                .andDo(print());
    }
    @Test
    @WithMockUser(authorities = {"ROLE_ADMIN"})
    void deleteMedia_ShouldReturn204_WhenAuthorized() throws Exception {
        mockMvc.perform(delete("/media/1")
                        .with(csrf())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andDo(print());
    }
    @Test
    @WithMockUser(authorities = {"ROLE_COUPLE"}) // Not allowed
    void deleteMedia_ShouldReturn403_WhenUnauthorizedRole() throws Exception {
        mockMvc.perform(delete("/media/1")
                        .with(csrf())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andDo(print());
    }
    @Test
    @WithMockUser(authorities = {"ROLE_ADMIN"})
    void deleteMedia_ShouldReturn404_WhenNotFound() throws Exception {
        doThrow(new ResourceNotFoundException("Media not found"))
                .when(mediaService).deleteMedia(99L);

        mockMvc.perform(delete("/media/99")
                        .with(csrf())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Media not found"))
                .andDo(print());
    }


}

