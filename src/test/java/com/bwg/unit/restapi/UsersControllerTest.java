/*
package com.bwg.unit.restapi;

import com.bwg.config.MethodSecurityConfig;
import com.bwg.domain.Users;
import com.bwg.exception.ResourceNotFoundException;
import com.bwg.model.AuthModel;
import com.bwg.model.UsersModel;
import com.bwg.restapi.UsersController;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import com.bwg.service.UsersService;
import com.bwg.unit.config.TestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UsersController.class)
@Import({TestConfig.class, MethodSecurityConfig.class})
public class UsersControllerTest extends BaseControllerTest{

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UsersService usersService;

    @Test
    @WithMockUser(authorities = {"ROLE_ADMIN"})
    void getUserById_ShouldReturn200_WhenAuthorized() throws Exception {
        Users userModel = new Users();
        userModel.setUserId(1L);
        userModel.setFirstName("Adil");
        userModel.setLastName("Waheed");

        doReturn(userModel).when(usersService).getUserById(eq(1L), any(AuthModel.class));

        mockMvc.perform(get("/users/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user_id").value(1))
                .andExpect(jsonPath("$.first_name").value("Adil"))
                .andExpect(jsonPath("$.last_name").value("Waheed"))
                .andDo(print());
    }

    @Test
    void getUserById_ShouldReturn401_WhenUnauthenticated() throws Exception {
        mockMvc.perform(get("/users/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }

    @Test
    @WithMockUser(authorities = {"ROLE_COUPLE"})
    void getUserById_ShouldReturn404_WhenUserNotFound() throws Exception {
        doThrow(new ResourceNotFoundException("User not found"))
                .when(usersService).getUserById(eq(999L), any(AuthModel.class));

        mockMvc.perform(get("/users/999")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("User not found"))
                .andDo(print());
    }

    @Test
    @WithMockUser(authorities = {"ROLE_ADMIN"})
    void updateUser_ShouldReturn200_WhenValid() throws Exception {
        Users updated = new Users();
        updated.setUserId(1L);
        updated.setFirstName("Updated");
        updated.setLastName("User");

        doReturn(updated).when(usersService).updateUser(eq(1L), any(UsersModel.class), any(AuthModel.class));

        mockMvc.perform(put("/users/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "first_name": "Updated",
                          "last_name": "User"
                        }
                    """)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.first_name").value("Updated"))
                .andExpect(jsonPath("$.last_name").value("User"))
                .andDo(print());
    }

    @Test
    @WithMockUser(authorities = {"ROLE_ADMIN"})
    void updateUser_ShouldReturn404_WhenUserNotFound() throws Exception {
        doThrow(new ResourceNotFoundException("User not found"))
                .when(usersService).updateUser(eq(99L), any(UsersModel.class), any(AuthModel.class));

        mockMvc.perform(put("/users/99")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "first_name": "Test",
                          "last_name": "User"
                        }
                    """)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("User not found"))
                .andDo(print());
    }

    @Test
    @WithMockUser(authorities = {"ROLE_ADMIN"})
    void deleteUser_ShouldReturn204_WhenAuthorized() throws Exception {
        mockMvc.perform(delete("/users/{userId}", 1L)
                        .with(csrf())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andDo(print());

        verify(usersService).deleteUser(eq(1L), any(AuthModel.class));
    }

    @Test
    @WithMockUser(authorities = {"ROLE_COUPLE"})
    void deleteUser_ShouldReturn403_WhenUnauthorizedRole() throws Exception {
        mockMvc.perform(delete("/users/{userId}", 1L)
                        .with(csrf())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andDo(print());
    }

    @Test
    @WithMockUser(authorities = {"ROLE_ADMIN"})
    void deleteUser_ShouldReturn404_WhenUserNotFound() throws Exception {
        doThrow(new ResourceNotFoundException("User not found"))
                .when(usersService).deleteUser(eq(999L), any(AuthModel.class));

        mockMvc.perform(delete("/users/{userId}", 999L)
                        .with(csrf())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("User not found"))
                .andDo(print());
    }


}
*/
