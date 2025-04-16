package com.bwg.unit.restapi;

import com.bwg.domain.Users;
import com.bwg.exception.BadRequestException;
import com.bwg.model.AuthModel;
import com.bwg.model.UsersModel;
import com.bwg.resolver.AuthPrincipalResolver;
import com.bwg.restapi.AuthenticationController;
import com.bwg.service.UsersService;
import com.bwg.unit.config.TestConfig;
import com.bwg.unit.service.util.TestDataFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthenticationController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import({TestConfig.class})
class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UsersService usersService;

    @MockBean
    private AuthPrincipalResolver authPrincipalArgumentResolver;
    @Autowired
    private ObjectMapper objectMapper;

    private UsersModel validInput;
    private Users createdUser;
    private AuthModel mockAuth;

    @BeforeEach
    void setup() {
        validInput = new UsersModel();
        validInput.setEmail("test@example.com");
        validInput.setFirstName("John");
        validInput.setLastName("Doe");
        validInput.setRole(Users.UserRole.admin);

        createdUser = new Users();
        createdUser.setUserId(1L);
        createdUser.setEmail("test@example.com");
        createdUser.setFirstName("John");
        createdUser.setLastName("Doe");

        mockAuth = TestDataFactory.buildAuthModel("1", "ADMIN");

        when(authPrincipalArgumentResolver.supportsParameter(any())).thenReturn(true);
        when(authPrincipalArgumentResolver.resolveArgument(any(), any(), any(), any()))
                .thenReturn(mockAuth);
    }

    @Test
    @DisplayName("POST /auth/register - success - should return 201 with created user")
    void testRegister_returns201WithUserModel() throws Exception {
        when(usersService.createUser(any(UsersModel.class), any(AuthModel.class)))
                .thenReturn(createdUser);

        mockMvc.perform(post("/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validInput)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.first_name").value("John"))
                .andExpect(jsonPath("$.last_name").value("Doe"));
    }

    @Test
    @DisplayName("POST /auth/register - failure - should return 400 when role is missing")
    void testRegister_returns400WhenRoleIsMissing() throws Exception {
        validInput.setRole(null);

        when(usersService.createUser(any(UsersModel.class), any(AuthModel.class)))
                .thenThrow(new BadRequestException("Role is required"));

        mockMvc.perform(post("/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validInput)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Role is required"));
    }


}
