package com.bwg.unit.restapi;

import com.bwg.config.MethodSecurityConfig;
import com.bwg.exception.ResourceNotFoundException;
import com.bwg.model.AuthModel;
import com.bwg.model.TagModel;
import com.bwg.restapi.TagController;

import com.bwg.service.TagService;
import com.bwg.unit.config.TestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.doThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import java.time.OffsetDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = TagController.class)
@Import({TestConfig.class, MethodSecurityConfig.class})
public class TagsControllerTest extends BaseControllerTest{

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TagService tagService;

    @Test
    @WithMockUser(authorities = {"ROLE_ADMIN"})
    void getTagById_ShouldReturn200_WhenAuthenticated() throws Exception {
        TagModel tag = new TagModel(1L, "Elegant", "ACTIVE", OffsetDateTime.now().withNano(0));

        doReturn(tag)
                .when(tagService).getTagById(eq(1L), any(AuthModel.class));

        mockMvc.perform(get("/tags/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tag_id").value(1L))
                .andExpect(jsonPath("$.name").value("Elegant"))
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.created_at").exists())
                .andDo(print());
    }

    @Test
    void getTagById_ShouldReturn403_WhenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/tags/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }

    @Test
    @WithMockUser(authorities = {"ROLE_USER"})
    void getTagById_ShouldReturn404_WhenTagNotFound() throws Exception {
        doThrow(new ResourceNotFoundException("Tag not found"))
                .when(tagService).getTagById(eq(99L), any(AuthModel.class));

        mockMvc.perform(get("/tags/99")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Tag not found"))
                .andDo(print());
    }

    @Test
    @WithMockUser(authorities = {"ROLE_ADMIN"})
    void createTag_ShouldReturn201_WhenAdmin() throws Exception {
        TagModel request = new TagModel(null, "Elegant", "ACTIVE", null);
        TagModel saved = new TagModel(1L, "Elegant", "ACTIVE", OffsetDateTime.now().withNano(0));

        doReturn(saved).when(tagService).createTag(any(TagModel.class), any(AuthModel.class));

        mockMvc.perform(post("/tags")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "tag_name": "Elegant",
                          "status": "ACTIVE"
                        }
                    """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tag_id").value(1))
                .andExpect(jsonPath("$.name").value("Elegant"))
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.created_at").exists())
                .andDo(print());
    }

    @Test
    @WithMockUser(authorities = {"ROLE_COUPLE"})
    void createTag_ShouldReturn403_WhenNotAdmin() throws Exception {
        mockMvc.perform(post("/tags")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "tag_name": "Elegant",
                          "status": "ACTIVE"
                        }
                    """))
                .andExpect(status().isForbidden())
                .andDo(print());
    }

    @Test
    @WithMockUser(authorities = {"ROLE_ADMIN"})
    void assignTagToService_ShouldReturn200_WhenAdmin() throws Exception {
        TagModel tag = new TagModel(1L, "Elegant", "ACTIVE", OffsetDateTime.now().withNano(0));

        doReturn(tag).when(tagService).assignTagToService(1L, 101L);

        mockMvc.perform(put("/tags/1/service/101")
                        .with(csrf())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tag_id").value(1))
                .andExpect(jsonPath("$.name").value("Elegant"))
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.created_at").exists())
                .andDo(print());
    }

    @Test
    @WithMockUser(authorities = {"ROLE_COUPLE"})
    void assignTagToService_ShouldReturn403_WhenUnauthorized() throws Exception {
        mockMvc.perform(put("/tags/1/service/101")
                        .with(csrf())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andDo(print());
    }

    @Test
    @WithMockUser(authorities = {"ROLE_ADMIN"})
    void assignTagToService_ShouldReturn404_WhenTagOrServiceNotFound() throws Exception {
        doThrow(new ResourceNotFoundException("Tag or service not found"))
                .when(tagService).assignTagToService(99L, 999L);

        mockMvc.perform(put("/tags/99/service/999")
                        .with(csrf())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Tag or service not found"))
                .andDo(print());
    }

    @Test
    @WithMockUser(authorities = {"ROLE_ADMIN"})
    void assignTagToCategory_ShouldReturn200_WhenAdmin() throws Exception {
        TagModel tag = new TagModel(1L, "Luxury", "ACTIVE", OffsetDateTime.now().withNano(0));

        doReturn(tag).when(tagService).assignTagToCategory(1L, 5L);

        mockMvc.perform(put("/tags/1/category/5")
                        .with(csrf())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tag_id").value(1))
                .andExpect(jsonPath("$.name").value("Luxury"))
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.created_at").exists())
                .andDo(print());
    }


    @Test
    @WithMockUser(authorities = {"ROLE_USER"})
    void assignTagToCategory_ShouldReturn403_WhenUnauthorized() throws Exception {
        mockMvc.perform(put("/tags/1/category/5")
                        .with(csrf())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andDo(print());
    }

    @Test
    @WithMockUser(authorities = {"ROLE_ADMIN"})
    void assignTagToCategory_ShouldReturn404_WhenTagOrCategoryNotFound() throws Exception {
        doThrow(new ResourceNotFoundException("Tag or category not found"))
                .when(tagService).assignTagToCategory(99L, 999L);

        mockMvc.perform(put("/tags/99/category/999")
                        .with(csrf())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Tag or category not found"))
                .andDo(print());
    }

    @Test
    @WithMockUser(authorities = {"ROLE_ADMIN"})
    void deleteTag_ShouldReturn204_WhenAdmin() throws Exception {
        mockMvc.perform(delete("/tags/1")
                        .with(csrf())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andDo(print());
    }

    @Test
    @WithMockUser(authorities = {"ROLE_COUPLE"})
    void deleteTag_ShouldReturn403_WhenUnauthorized() throws Exception {
        mockMvc.perform(delete("/tags/1")
                        .with(csrf())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andDo(print());
    }

    @Test
    @WithMockUser(authorities = {"ROLE_ADMIN"})
    void deleteTag_ShouldReturn404_WhenTagNotFound() throws Exception {
        doThrow(new ResourceNotFoundException("Tag not found"))
                .when(tagService).deleteTag(eq(99L), any());

        mockMvc.perform(delete("/tags/99")
                        .with(csrf())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Tag not found"))
                .andDo(print());
    }


}
