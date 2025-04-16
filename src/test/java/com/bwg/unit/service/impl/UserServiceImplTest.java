package com.bwg.unit.service.impl;

import com.bwg.domain.Users;
import com.bwg.exception.BadRequestException;
import com.bwg.exception.ResourceAlreadyExistsException;
import com.bwg.exception.ResourceNotFoundException;
import com.bwg.model.AuthModel;
import com.bwg.model.UsersModel;
import com.bwg.repository.UsersRepository;
import com.bwg.service.impl.UsersServiceImpl;
import com.bwg.util.SecurityUtils;
import com.querydsl.core.BooleanBuilder;
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

import java.util.List;
import java.util.Optional;

import static com.bwg.unit.service.util.TestDataFactory.buildAuthModel;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class UserServiceImplTest {

    @Mock
    private UsersRepository usersRepository;

    @InjectMocks
    private UsersServiceImpl userService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateUser_successfullyCreatesUser() {
        UsersModel usersModel = new UsersModel();
        usersModel.setEmail("test@example.com");
        usersModel.setFirstName("Test");
        usersModel.setLastName("User");
        usersModel.setPassword("secret");
        usersModel.setRole(Users.UserRole.admin);

        AuthModel authModel = buildAuthModel("user-123", "ADMIN");


        when(usersRepository.findByEmailIgnoreCase("test@example.com")).thenReturn(null);
        when(usersRepository.save(any(Users.class))).thenAnswer(invocation -> {
            Users user = invocation.getArgument(0);
            user.setUserId(1L);
            return user;
        });

        Users result = userService.createUser(usersModel, authModel);

        assertNotNull(result);
        assertEquals("test@example.com", result.getEmail());
        assertEquals("Test", result.getFirstName());
        assertEquals("User", result.getLastName());
        assertNotNull(result.getCreatedAt());
    }

    @Test
    void testCreateUser_throwsIfUserAlreadyExists() {

        UsersModel usersModel = new UsersModel();
        usersModel.setEmail("test@example.com");
        usersModel.setRole(Users.UserRole.admin);

        AuthModel authModel = buildAuthModel(
                "user-123", "ADMIN");

        when(usersRepository.findByEmailIgnoreCase("test@example.com")).thenReturn(new Users());

        assertThrows(ResourceAlreadyExistsException.class, () -> userService.createUser(usersModel, authModel));
    }

    @Test
    void testCreateUser_throwsIfRoleMissing() {
        UsersModel usersModel = new UsersModel();
        usersModel.setEmail("test@example.com");

        AuthModel authModel = buildAuthModel(
                "user-123", "ADMIN");

        when(usersRepository.findByEmailIgnoreCase("test@example.com")).thenReturn(null);

        assertThrows(BadRequestException.class, () -> userService.createUser(usersModel, authModel));
    }

    @Test
    void testGetCurrentUser_returnsUserIfFound() {
        AuthModel authModel = buildAuthModel(
                "1", "ADMIN");

        Users mockUser = new Users();
        mockUser.setUserId(1L);
        mockUser.setFirstName("John");

        when(usersRepository.findById(1L)).thenReturn(Optional.of(mockUser));

        Users result = userService.getCurrentUser(authModel);

        assertNotNull(result);
        assertEquals(1L, result.getUserId());
        assertEquals("John", result.getFirstName());
    }

    @Test
    void testGetCurrentUser_throwsIfNotFound() {
        AuthModel authModel = buildAuthModel(
                "1", "ADMIN");

        when(usersRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> userService.getCurrentUser(authModel));
    }


    @Test
    void testGetAllUsers_returnsPagedUsers() {
        String search = "john";
        Long userId = 1L;
        Long vendorId = 10L;
        Pageable pageable = PageRequest.of(0, 10);

        Users user1 = new Users();
        user1.setUserId(1L);
        user1.setFirstName("John");

        Page<Users> mockPage = new PageImpl<>(List.of(user1));

        when(usersRepository.findAll(any(BooleanBuilder.class), eq(pageable)))
                .thenReturn(mockPage);

        Page<Users> result = userService.getAllUsers(search, userId, vendorId, pageable);
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("John", result.getContent().get(0).getFirstName());
    }

    @Test
    void testGetAllUsers_withNullFilters() {
        Pageable pageable = PageRequest.of(0, 5);

        Users user1 = new Users();
        user1.setUserId(2L);
        user1.setFirstName("Alice");

        Page<Users> mockPage = new PageImpl<>(List.of(user1));

        when(usersRepository.findAll(any(BooleanBuilder.class), eq(pageable)))
                .thenReturn(mockPage);

        Page<Users> result = userService.getAllUsers(null, null, null, pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("Alice", result.getContent().get(0).getFirstName());
    }

    @Test
    void testGetUserById_returnsUserIfFound() {
        Long userId = 1L;
        AuthModel authModel = buildAuthModel(
                "1", "ADMIN");

        Users user = new Users();
        user.setUserId(1L);
        user.setFirstName("John");

        try (MockedStatic<SecurityUtils> mockedStatic = mockStatic(SecurityUtils.class)) {
            mockedStatic
                    .when(() -> SecurityUtils.checkOwnerOrAdmin("1", authModel))
                    .thenCallRealMethod();

            when(usersRepository.findById(userId)).thenReturn(Optional.of(user));

            Users result = userService.getUserById(userId, authModel);

            assertNotNull(result);
            assertEquals("John", result.getFirstName());

            mockedStatic.verify(() -> SecurityUtils.checkOwnerOrAdmin("1", authModel), times(1));
        }
    }


    @Test
    void testGetUserById_throwsIfUserNotFound() {
        Long userId = 2L;
        AuthModel authModel = buildAuthModel(
                "user-123", "ADMIN");

        try (MockedStatic<SecurityUtils> mockedStatic = mockStatic(SecurityUtils.class)) {

            mockedStatic
                    .when(() -> SecurityUtils.checkOwnerOrAdmin("2", authModel))
                    .thenAnswer(invocation -> null);

            when(usersRepository.findById(userId)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(userId, authModel));

            mockedStatic.verify(() -> SecurityUtils.checkOwnerOrAdmin("2", authModel), times(1));
        }

    }
    @Test
    void testUpdateUser_successfullyUpdatesUser() {
        Long userId = 1L;
        AuthModel authModel = buildAuthModel(
                "user-123", "ADMIN");

        UsersModel updateRequest = new UsersModel();
        updateRequest.setFirstName("UpdatedName");
        updateRequest.setLastName("UpdatedLastName");

        Users existingUser = new Users();
        existingUser.setUserId(userId);
        existingUser.setEmail("john@example.com");
        existingUser.setFirstName("OldName");
        existingUser.setLastName("OldLastName");

        Users savedUser = new Users();
        savedUser.setUserId(userId);
        savedUser.setEmail("john@example.com");
        savedUser.setFirstName("UpdatedName");
        savedUser.setLastName("UpdatedLastName");

        try (MockedStatic<SecurityUtils> mockedStatic = mockStatic(SecurityUtils.class)) {
            mockedStatic
                    .when(() -> SecurityUtils.checkOwnerOrAdmin("1", authModel))
                    .thenAnswer(invocation -> null);

            when(usersRepository.findById(userId)).thenReturn(Optional.of(existingUser));
            when(usersRepository.save(any(Users.class))).thenReturn(savedUser);

            Users result = userService.updateUser(userId, updateRequest, authModel);

            assertNotNull(result);
            assertEquals("UpdatedName", result.getFirstName());
            assertEquals("UpdatedLastName", result.getLastName());
            mockedStatic.verify(() -> SecurityUtils.checkOwnerOrAdmin("1", authModel), times(1));
        }
    }

    @Test
    void testUpdateUser_throwsIfUserNotFound() {
        Long userId = 2L;
        AuthModel authModel = buildAuthModel(
                "user-123", "ADMIN");

        UsersModel updateRequest = new UsersModel();
        updateRequest.setFirstName("Test");

        try (MockedStatic<SecurityUtils> mockedStatic = mockStatic(SecurityUtils.class)) {
            mockedStatic
                    .when(() -> SecurityUtils.checkOwnerOrAdmin("2", authModel))
                    .thenAnswer(invocation -> null);

            when(usersRepository.findById(userId)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> userService.updateUser(userId, updateRequest, authModel));

            mockedStatic.verify(() -> SecurityUtils.checkOwnerOrAdmin("2", authModel), times(1));
        }
    }

    @Test
    void testDeleteUser_successfullyDeletes() {
        Long userId = 1L;
        AuthModel authModel = buildAuthModel(
                "user-123", "ADMIN");

        Users existingUser = new Users();
        existingUser.setUserId(userId);
        existingUser.setEmail("john@example.com");

        try (MockedStatic<SecurityUtils> mockedStatic = mockStatic(SecurityUtils.class)) {
            mockedStatic
                    .when(() -> SecurityUtils.checkOwnerOrAdmin("1", authModel))
                    .thenAnswer(invocation -> null);

            when(usersRepository.findById(userId)).thenReturn(Optional.of(existingUser));

            userService.deleteUser(userId, authModel);

            verify(usersRepository, times(1)).delete(existingUser);
            mockedStatic.verify(() -> SecurityUtils.checkOwnerOrAdmin("1", authModel), times(2));
        }
    }

    @Test
    void testDeleteUser_throwsIfUserNotFound() {
        Long userId = 99L;
        AuthModel authModel = buildAuthModel(
                "user-123", "ADMIN");

        try (MockedStatic<SecurityUtils> mockedStatic = mockStatic(SecurityUtils.class)) {
            mockedStatic
                    .when(() -> SecurityUtils.checkOwnerOrAdmin("99", authModel))
                    .thenAnswer(invocation -> null);

            when(usersRepository.findById(userId)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> userService.deleteUser(userId, authModel));

            verify(usersRepository, never()).delete(any());
        }
    }

}
