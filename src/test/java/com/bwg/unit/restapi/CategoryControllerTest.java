package com.bwg.unit.restapi;

import com.bwg.config.MethodSecurityConfig;
import com.bwg.exception.ResourceAlreadyExistsException;
import com.bwg.exception.ResourceNotFoundException;
import com.bwg.model.*;
import com.bwg.resolver.AuthPrincipalResolver;
import com.bwg.restapi.CategoriesController;
import com.bwg.service.CategoriesService;
import com.bwg.unit.config.TestConfig;
import com.bwg.unit.service.util.TestDataFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.anonymous;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

@WebMvcTest(controllers = CategoriesController.class)
@Import({TestConfig.class, MethodSecurityConfig.class})
class CategoryControllerTest extends BaseControllerTest{
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoriesService categoriesService;

    @Test
    @WithMockUser
    void getCategoryById_ShouldReturnCategory() throws Exception {
        CategoriesModel category = new CategoriesModel(
                1L,
                "UCAT123",
                "Weddings",
                OffsetDateTime.now().withNano(0),
                OffsetDateTime.now().withNano(0),
                List.of(new ServicesModel(10L, "Photography", 500.0, 1500.0, "Available", 1L)),
                Set.of(new TagModel(1L, "Traditional","ACTIVE",OffsetDateTime.now().withNano(0)))         // simplified tag
        );

        doReturn(category).when(categoriesService).getCategoryById(1L);

        mockMvc.perform(get("/categories/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.category_id").value(1))
                .andExpect(jsonPath("$.category_name").value("Weddings"))
                .andExpect(jsonPath("$.created_at").exists())
                .andExpect(jsonPath("$.u_category_id").doesNotExist())  // Ignored
                .andExpect(jsonPath("$.updated_at").doesNotExist())     // Ignored
                .andExpect(jsonPath("$.services[0].service_id").value(10))
                .andExpect(jsonPath("$.services[0].service_name").value("Photography"))
                .andExpect(jsonPath("$.services[0].price_min").value(500.0))
                .andExpect(jsonPath("$.services[0].price_max").value(1500.0))
                .andExpect(jsonPath("$.services[0].availability").value("Available"))
                .andExpect(jsonPath("$.services[0].category_id").value(1))
                .andExpect(jsonPath("$.tags[0].tag_id").value(1))
                .andExpect(jsonPath("$.tags[0].name").value("Traditional"))
                .andDo(print());
    }

    @Test
    @WithMockUser
    void getCategoryById_WhenNotFound_ShouldReturn404() throws Exception {
        doThrow(new ResourceNotFoundException("Category not found"))
                .when(categoriesService).getCategoryById(99L);

        mockMvc.perform(get("/categories/99")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Category not found"))
                .andDo(print());
    }
    @Test
    @WithMockUser(username = "admin", authorities = {"ROLE_ADMIN"})
    void createCategory_ShouldReturnCreatedCategory_WhenAdmin() throws Exception {
        CategoriesModel inputModel = new CategoriesModel(
                null,
                null,
                "Weddings",
                null,
                null,
                List.of(),
                Set.of()
        );

        CategoriesModel savedModel = new CategoriesModel(
                1L,
                "UCAT123",
                "Weddings",
                OffsetDateTime.now().withNano(0),
                OffsetDateTime.now().withNano(0),
                List.of(),
                Set.of()
        );

        doReturn(savedModel)
                .when(categoriesService)
                .createCategory(any(CategoriesModel.class));

        mockMvc.perform(post("/categories")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "category_name": "Weddings",
                          "services": [],
                          "tags": []
                        }
                    """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.category_id").value(1))
                .andExpect(jsonPath("$.category_name").value("Weddings"))
                .andExpect(jsonPath("$.created_at").exists())
                .andDo(print());
    }
    @Test
    @WithMockUser(username = "admin", authorities = {"ROLE_COUPLE"})
    void createCategory_ShouldReturn403_WhenNotAdmin() throws Exception {
        mockMvc.perform(post("/categories")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                      "category_name": "Weddings",
                      "services": [],
                      "tags": []
                    }
                """))
                .andExpect(status().isForbidden())
                .andDo(print());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ROLE_ADMIN"})
    void createCategory_ShouldReturn409_WhenCategoryAlreadyExists() throws Exception {
        CategoriesModel duplicateCategory = new CategoriesModel(
                1L,
                "UCAT123",
                "Weddings",
                OffsetDateTime.now().withNano(0),
                OffsetDateTime.now().withNano(0),
                List.of(),
                Set.of()
        );

        doThrow(new ResourceAlreadyExistsException("Category name already exists."))
                .when(categoriesService).createCategory(any(CategoriesModel.class));

        mockMvc.perform(post("/categories")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "category_name": "Weddings",
                          "services": [],
                          "tags": []
                        }
                    """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Category name already exists."))
                .andDo(print());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ROLE_ADMIN"})
    void updateCategory_ShouldReturn200() throws Exception {
        Long categoryId = 1L;
        CategoriesModel updatedCategory = new CategoriesModel(
                categoryId,
                "UCAT123",
                "Updated Weddings",
                OffsetDateTime.now().withNano(0),
                OffsetDateTime.now().withNano(0),
                List.of(new ServicesModel(10L, "Photography", 500.0, 1500.0, "Available", categoryId)),
                Set.of(new TagModel(1L, "Traditional", "ACTIVE", OffsetDateTime.now().withNano(0)))
        );
        doReturn(updatedCategory)
                .when(categoriesService)
                .updateCategory(eq(categoryId), any(CategoriesModel.class));


        mockMvc.perform(put("/categories/{categoryId}", categoryId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "category_name": "Updated Weddings",
                          "services": [
                            {
                              "service_id": 10,
                              "service_name": "Photography",
                              "price_min": 500.0,
                              "price_max": 1500.0,
                              "availability": "Available",
                              "category_id": 1
                            }
                          ],
                          "tags": [
                            {
                              "tag_id": 1,
                              "tag_name": "Traditional",
                              "status": "ACTIVE",
                              "created_at": "2025-04-15T15:00:00Z"
                            }
                          ]
                        }
                    """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.category_name").value("Updated Weddings"))
                .andExpect(jsonPath("$.services[0].service_name").value("Photography"))
                .andExpect(jsonPath("$.tags[0].name").value("Traditional"))
                .andDo(print());
    }

    @Test
    @WithMockUser(username = "user", authorities = {"ROLE_VENDOR"})
    void updateCategory_ShouldReturn403_WhenNotAdmin() throws Exception {
        mockMvc.perform(put("/categories/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "category_name": "Unauthorized Update",
                          "services": [],
                          "tags": []
                        }
                    """))
                .andExpect(status().isForbidden()) // 🔒 Expected 403
                .andDo(print());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ROLE_ADMIN"})
    void updateCategory_ShouldReturn404_WhenNotFound() throws Exception {
        Long categoryId = 99L;

        doThrow(new ResourceNotFoundException("Category not found"))
                .when(categoriesService)
                .updateCategory(eq(categoryId), any(CategoriesModel.class));

        mockMvc.perform(put("/categories/{categoryId}", categoryId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "category_name": "Missing Category",
                          "services": [],
                          "tags": []
                        }
                    """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Category not found"))
                .andDo(print());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ROLE_ADMIN"})
    void updateCategory_ShouldReturn409_WhenDuplicateName() throws Exception {
        Long categoryId = 1L;

        doThrow(new ResourceAlreadyExistsException("Category name already exists."))
                .when(categoriesService)
                .updateCategory(eq(categoryId), any(CategoriesModel.class));

        mockMvc.perform(put("/categories/{categoryId}", categoryId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "category_name": "Weddings",
                          "services": [],
                          "tags": []
                        }
                    """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Category name already exists."))
                .andDo(print());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ROLE_ADMIN"})
    void deleteCategory_ShouldReturn204_WhenSuccessful() throws Exception {
        mockMvc.perform(delete("/categories/{categoryId}", 1L)
                        .with(csrf())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent()) // ✅ 204
                .andDo(print());
    }

    @Test
    @WithMockUser(username = "user", authorities = {"ROLE_VENDOR"})
    void deleteCategory_ShouldReturn403_WhenNotAdmin() throws Exception {
        mockMvc.perform(delete("/categories/{categoryId}", 1L)
                        .with(csrf())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden()) // 🔒 403
                .andDo(print());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ROLE_ADMIN"})
    void deleteCategory_ShouldReturn404_WhenNotFound() throws Exception {
        doThrow(new ResourceNotFoundException("Category not found"))
                .when(categoriesService)
                .deleteCategory(999L);

        mockMvc.perform(delete("/categories/{categoryId}", 999L)
                        .with(csrf())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Category not found"))
                .andDo(print());
    }


}

