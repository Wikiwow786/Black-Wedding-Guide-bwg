package com.bwg.unit.restapi;

import com.bwg.config.MethodSecurityConfig;
import com.bwg.domain.Categories;
import com.bwg.domain.Services;
import com.bwg.exception.ResourceNotFoundException;
import com.bwg.model.ServicesModel;
import com.bwg.restapi.ReviewsController;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import com.bwg.restapi.ServicesController;
import com.bwg.service.ServicesService;
import com.bwg.unit.config.TestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ServicesController.class)
@Import({TestConfig.class, MethodSecurityConfig.class})
public class ServicesControllerTest extends BaseControllerTest{
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ServicesService servicesService;

    @Test
    @WithMockUser
    void getServicesById_ShouldReturn200_WhenExists() throws Exception {
        Services service = new Services();
        service.setServiceId(1L);
        service.setServiceName("Photography");
        service.setPriceMin(500.0);
        service.setPriceMax(1500.0);
        service.setAvailability("Available");
        Categories category = new Categories();
        category.setCategoryId(10L);
        service.setCategory(category);

        doReturn(service)
                .when(servicesService).getServiceById(1L);

        mockMvc.perform(get("/services/{serviceId}", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.service_id").value(1L))
                .andExpect(jsonPath("$.service_name").value("Photography"))
                .andExpect(jsonPath("$.price_min").value(500.0))
                .andExpect(jsonPath("$.price_max").value(1500.0))
                .andExpect(jsonPath("$.availability").value("Available"))
                .andExpect(jsonPath("$.category_id").value(10L))
                .andDo(print());
    }

    @Test
    @WithMockUser
    void getServicesById_ShouldReturn404_WhenNotFound() throws Exception {
        doThrow(new ResourceNotFoundException("Service not found"))
                .when(servicesService).getServiceById(99L);

        mockMvc.perform(get("/services/{serviceId}", 99L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Service not found"))
                .andDo(print());
    }

    @Test
    @WithMockUser(authorities = {"ROLE_VENDOR"})
    void createService_ShouldReturn201_WhenAuthorized() throws Exception {
        Services service = new Services();
        service.setServiceId(1L);
        service.setServiceName("Photography");
        service.setPriceMin(500.0);
        service.setPriceMax(1500.0);
        service.setAvailability("Available");
        Categories category = new Categories();
        category.setCategoryId(10L);
        service.setCategory(category);

        doReturn(service)
                .when(servicesService).createService(any(ServicesModel.class));

        mockMvc.perform(post("/services")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "service_name": "Photography",
                          "price_min": 500.0,
                          "price_max": 1500.0,
                          "availability": "Available",
                          "category_id": 10
                        }
                    """)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.service_id").value(1L))
                .andExpect(jsonPath("$.service_name").value("Photography"))
                .andExpect(jsonPath("$.price_min").value(500.0))
                .andExpect(jsonPath("$.price_max").value(1500.0))
                .andExpect(jsonPath("$.availability").value("Available"))
                .andExpect(jsonPath("$.category_id").value(10))
                .andDo(print());
    }

    @Test
    @WithMockUser(authorities = {"ROLE_COUPLE"})
    void createService_ShouldReturn403_WhenUnauthorizedRole() throws Exception {
        mockMvc.perform(post("/services")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "service_name": "Photography",
                          "price_min": 500.0,
                          "price_max": 1500.0,
                          "availability": "Available",
                          "category_id": 10
                        }
                    """)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andDo(print());
    }


    @Test
    @WithMockUser(authorities = {"ROLE_ADMIN"})
    void updateService_ShouldReturn200_WhenAuthorized() throws Exception {
        Services updated = new Services();
        updated.setServiceId(1L);
        updated.setServiceName("Updated Photography");
        updated.setPriceMin(600.0);
        updated.setPriceMax(1600.0);
        updated.setAvailability("Available");
        Categories category = new Categories();
        category.setCategoryId(10L);
        updated.setCategory(category);

        doReturn(updated)
                .when(servicesService).updateService(eq(1L), any(ServicesModel.class));

        mockMvc.perform(put("/services/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "service_name": "Updated Photography",
                          "price_min": 600.0,
                          "price_max": 1600.0,
                          "availability": "Available",
                          "category_id": 10
                        }
                    """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.service_name").value("Updated Photography"))
                .andExpect(jsonPath("$.price_min").value(600.0))
                .andExpect(jsonPath("$.price_max").value(1600.0))
                .andExpect(jsonPath("$.availability").value("Available"))
                .andExpect(jsonPath("$.category_id").value(10))
                .andDo(print());
    }

    @Test
    @WithMockUser(authorities = {"ROLE_COUPLE"})
    void updateService_ShouldReturn403_WhenUnauthorizedRole() throws Exception {
        mockMvc.perform(put("/services/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "service_name": "Updated Photography",
                          "price_min": 600.0,
                          "price_max": 1600.0,
                          "availability": "Available",
                          "category_id": 10
                        }
                    """))
                .andExpect(status().isForbidden())
                .andDo(print());
    }
    @Test
    @WithMockUser(authorities = {"ROLE_ADMIN"})
    void updateService_ShouldReturn404_WhenServiceNotFound() throws Exception {
        doThrow(new ResourceNotFoundException("Service not found"))
                .when(servicesService).updateService(eq(99L), any(ServicesModel.class));

        mockMvc.perform(put("/services/99")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "service_name": "Photography",
                          "price_min": 500.0,
                          "price_max": 1500.0,
                          "availability": "Available",
                          "category_id": 10
                        }
                    """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Service not found"))
                .andDo(print());
    }

    @Test
    @WithMockUser(authorities = {"ROLE_ADMIN"})
    void deleteService_ShouldReturn204_WhenAuthorized() throws Exception {
        mockMvc.perform(delete("/services/1")
                        .with(csrf())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andDo(print());

        verify(servicesService).deleteService(1L);
    }

    @Test
    @WithMockUser(authorities = {"ROLE_COUPLE"})
    void deleteService_ShouldReturn403_WhenUnauthorizedRole() throws Exception {
        mockMvc.perform(delete("/services/1")
                        .with(csrf())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andDo(print());

        verify(servicesService, never()).deleteService(anyLong());
    }

    @Test
    @WithMockUser(authorities = {"ROLE_ADMIN"})
    void deleteService_ShouldReturn404_WhenServiceNotFound() throws Exception {
        doThrow(new ResourceNotFoundException("Service not found"))
                .when(servicesService).deleteService(99L);

        mockMvc.perform(delete("/services/99")
                        .with(csrf())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Service not found"))
                .andDo(print());
    }


}
