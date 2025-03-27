package com.bwg.service.impl;

import com.bwg.domain.QUsers;
import com.bwg.domain.Services;
import com.bwg.domain.Users;
import com.bwg.exception.BadRequestException;
import com.bwg.exception.ResourceAlreadyExistsException;
import com.bwg.exception.ResourceNotFoundException;
import com.bwg.model.AuthModel;
import com.bwg.model.UsersModel;
import com.bwg.repository.UsersRepository;
import com.bwg.service.UsersService;
import com.bwg.util.BeanUtil;
import com.bwg.util.SecurityUtils;
import com.querydsl.core.BooleanBuilder;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.bwg.logger.Logger.format;
import static com.bwg.logger.Logger.info;
import static com.bwg.logger.LoggingEvent.LOG_SERVICE_OR_REPOSITORY;

@Service
public class UsersServiceImpl implements UsersService {

    @Autowired
    private UsersRepository usersRepository;

    @Override
    public Page<Users> getAllUsers(String search,Long userId,Long vendorId,Pageable pageable) {
        info(LOG_SERVICE_OR_REPOSITORY, "Fetching All Users", this);
        BooleanBuilder filter = new BooleanBuilder();
        if(StringUtils.isNotBlank(search)){
            filter.and(QUsers.users.firstName.containsIgnoreCase(search)
                    .or(QUsers.users.lastName.containsIgnoreCase(search)));
        }
        if(userId != null){
            filter.and(QUsers.users.userId.eq(userId));
        }
        if(vendorId != null){
            filter.and(QUsers.users.vendor.vendorId.eq(vendorId));
        }
        return usersRepository.findAll(filter,pageable);
    }

    @Override
    public Users getUserById(Long userId, AuthModel authModel) {
        info(LOG_SERVICE_OR_REPOSITORY, "Fetching User by Id {0}", userId);
        SecurityUtils.checkOwnerOrAdmin(userId.toString(), authModel);
        return usersRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @Override
    public Users getCurrentUser(AuthModel authModel) {
        info(LOG_SERVICE_OR_REPOSITORY, "Fetching Current user", authModel.userId());
        return usersRepository.findById(Long.parseLong(authModel.userId()))
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @Override
    public Users createUser(UsersModel usersModel,AuthModel authModel) {
        info(LOG_SERVICE_OR_REPOSITORY, format("Creating User..."), this);
        String email = Optional.ofNullable(usersModel.getEmail()).orElse(authModel.email());
        String firstName = Optional.ofNullable(usersModel.getFirstName()).orElse(authModel.firstName());
        String lastName = Optional.ofNullable(usersModel.getFirstName()).orElse(authModel.lastName() );

        if (usersRepository.findByEmailIgnoreCase(email) != null) {
            throw new ResourceAlreadyExistsException("User already exists with email: " + email);
        }

        if (usersModel.getRole() == null) {
            throw new BadRequestException("Role is required.");
        }

        Users users = new Users();
        BeanUtils.copyProperties(usersModel, users);
        users.setEmail(email);
        users.setFirstName(firstName);
        users.setLastName(lastName);
        if (usersModel.getPassword() != null && !usersModel.getPassword().isEmpty()) {
            users.setPasswordHash(encodePassword(usersModel.getPassword()));
        }
        users.setCreatedAt(OffsetDateTime.now());
        return usersRepository.save(users);
    }

    @Override
    public Users updateUser(Long userId, UsersModel usersModel,AuthModel authModel) {
        Objects.requireNonNull(userId, "User ID cannot be null");

        info(LOG_SERVICE_OR_REPOSITORY, format("Updating user info for userId {0}", userId), this);
        SecurityUtils.checkOwnerOrAdmin(userId.toString(), authModel);
        var user = usersRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        BeanUtils.copyProperties(usersModel, user, "userId", "email", "passwordHash", "createdAt", "role");

        user.setUpdatedAt(OffsetDateTime.now());
        return usersRepository.save(user);
    }

    @Override
    public void deleteUser(Long userId, AuthModel authModel) {
        info(LOG_SERVICE_OR_REPOSITORY, format("Delete user information for userId {0} ", userId), this);
        SecurityUtils.checkOwnerOrAdmin(userId.toString(), authModel);
        Users users = getUserById(userId, authModel);
        usersRepository.delete(users);
    }

    private String encodePassword(String password) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);
        return passwordEncoder.encode(password);
    }
}
