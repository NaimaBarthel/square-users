package com.naima.square_users.services;

import com.naima.square_users.controllers.dto.UserCreationDto;
import com.naima.square_users.dao.entities.UserEntity;

import java.util.Optional;

public interface UserService {
    UserEntity createUser(UserCreationDto dto);
    Optional<UserEntity> getUserById(String id);
    void deleteUser(String id);
    boolean isUserValid(String id);
}
