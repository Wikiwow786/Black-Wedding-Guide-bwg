package com.bwg.service;

import com.bwg.domain.Users;
import com.bwg.model.AuthModel;
import com.bwg.model.UsersModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UsersService {
    Page<Users> getAllUsers(String search, Long userId, Long vendorId, Pageable pageable);

    Users getUserById(Long userId, AuthModel authModel);

    Users getCurrentUser(AuthModel authModel);

    Users createUser(UsersModel usersModel, AuthModel authModel);

    Users updateUser(Long userId, UsersModel usersModel,AuthModel authModel);

    void deleteUser(Long userId, AuthModel authModel);
}
