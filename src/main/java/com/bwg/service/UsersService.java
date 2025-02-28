package com.bwg.service;

import com.bwg.domain.Users;
import com.bwg.model.AuthModel;
import com.bwg.model.UsersModel;

import java.util.List;

public interface UsersService {
    List<Users> getAllUsers();

    Users getUserById(Long userId);

    Users createUser(UsersModel usersModel);

    Users updateUser(Long userId, UsersModel usersModel);

    void deleteUser(Long userId);
}
