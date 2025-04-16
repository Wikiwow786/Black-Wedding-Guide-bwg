/*
package com.bwg.unit.restapi;

import com.bwg.config.MethodSecurityConfig;

import com.bwg.domain.Users;
import com.bwg.domain.Vendors;
import com.bwg.exception.ResourceNotFoundException;
import com.bwg.model.AuthModel;
import com.bwg.model.VendorsModel;
import com.bwg.restapi.VendorsController;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import com.bwg.service.VendorsService;
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

@WebMvcTest(controllers = VendorsController.class)
@Import({TestConfig.class, MethodSecurityConfig.class})
public class VendorsControllerTest extends BaseControllerTest{

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VendorsService vendorsService;

    @Test
    @WithMockUser
    void getVendorById_ShouldReturn200_WhenExists() throws Exception {
        Vendors vendor = new Vendors();
        Users users = new Users();
        users.setUserId(1L);
        users.setFirstName("Adil");
        users.setLastName("Waheed");
        vendor.setVendorId(1L);
        vendor.setUser(users);
        vendor.setBusinessName("Dream Events");

        doReturn(vendor).when(vendorsService).getVendorById(1L);

        mockMvc.perform(get("/vendors/{vendorId}", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.vendor_id").value(1))
                .andExpect(jsonPath("$.business_name").value("Dream Events"))
                .andDo(print());
    }

    @Test
    @WithMockUser
    void getVendorById_ShouldReturn404_WhenNotFound() throws Exception {
        doThrow(new ResourceNotFoundException("Vendor not found"))
                .when(vendorsService).getVendorById(99L);

        mockMvc.perform(get("/vendors/{vendorId}", 99L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Vendor not found"))
                .andDo(print());
    }

    @Test
    @WithMockUser(authorities = {"ROLE_VENDOR"})
    void createVendor_ShouldReturn201_WhenAuthorized() throws Exception {
        Vendors vendor = new Vendors();
        Users users = new Users();
        users.setUserId(1L);
        users.setFirstName("Adil");
        users.setLastName("Waheed");
        vendor.setVendorId(1L);
        vendor.setBusinessName("Dream Events");
        vendor.setUser(users);

        VendorsModel inputModel = new VendorsModel();
        inputModel.setBusinessName("Dream Events");
        inputModel.setUserId(1L);

        doReturn(vendor).when(vendorsService).createVendor(any(VendorsModel.class));

        mockMvc.perform(post("/vendors")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "business_name": "Dream Events",
                            "user_id": 1
                        }
                    """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.vendor_id").value(1))
                .andExpect(jsonPath("$.business_name").value("Dream Events"))
                .andDo(print());
    }

    @Test
    @WithMockUser(authorities = {"ROLE_COUPLE"})
    void createVendor_ShouldReturn403_WhenUnauthorized() throws Exception {
        mockMvc.perform(post("/vendors")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "vendor_name": "Dream Events"
                        }
                    """))
                .andExpect(status().isForbidden())
                .andDo(print());
    }

    @Test
    @WithMockUser
    void updateVendor_ShouldReturn200_WhenAuthenticated() throws Exception {
        Long vendorId = 1L;
        Users users = new Users();
        users.setUserId(1L);
        users.setFirstName("Adil");
        users.setLastName("Waheed");
        Vendors vendor = new Vendors();
        vendor.setVendorId(vendorId);
        vendor.setBusinessName("Updated Events");
        vendor.setUser(users);

        doReturn(vendor).when(vendorsService).updateVendor(eq(vendorId), any(VendorsModel.class), any(AuthModel.class));

        mockMvc.perform(put("/vendors/{vendorId}", vendorId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "business_name": "Updated Events",
                            "user_id": 1
                        }
                    """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.vendor_id").value(1))
                .andExpect(jsonPath("$.business_name").value("Updated Events"))
                .andDo(print());
    }

    @Test
    void updateVendor_ShouldReturn401_WhenUnauthenticated() throws Exception {
        mockMvc.perform(put("/vendors/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "vendor_name": "Updated Events"
                        }
                    """))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }

    @Test
    @WithMockUser
    void updateVendor_ShouldReturn404_WhenVendorNotFound() throws Exception {
        doThrow(new ResourceNotFoundException("Vendor not found"))
                .when(vendorsService).updateVendor(eq(99L), any(VendorsModel.class), any(AuthModel.class));

        mockMvc.perform(put("/vendors/{vendorId}", 99L)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "vendor_name": "Nonexistent Vendor"
                        }
                    """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Vendor not found"))
                .andDo(print());
    }

    @Test
    @WithMockUser(authorities = {"ROLE_ADMIN"})
    void deleteVendor_ShouldReturn204_WhenAuthorized() throws Exception {
        mockMvc.perform(delete("/vendors/{vendorId}", 1L)
                        .with(csrf())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andDo(print());

        verify(vendorsService).deleteVendor(eq(1L), any(AuthModel.class));
    }

    @Test
    @WithMockUser(authorities = {"ROLE_COUPLE"})
    void deleteVendor_ShouldReturn403_WhenUnauthorizedRole() throws Exception {
        mockMvc.perform(delete("/vendors/{vendorId}", 1L)
                        .with(csrf())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andDo(print());
    }

    @Test
    @WithMockUser(authorities = {"ROLE_ADMIN"})
    void deleteVendor_ShouldReturn404_WhenVendorNotFound() throws Exception {
        doThrow(new ResourceNotFoundException("Vendor not found"))
                .when(vendorsService).deleteVendor(eq(99L), any(AuthModel.class));

        mockMvc.perform(delete("/vendors/{vendorId}", 99L)
                        .with(csrf())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Vendor not found"))
                .andDo(print());
    }



}
*/
