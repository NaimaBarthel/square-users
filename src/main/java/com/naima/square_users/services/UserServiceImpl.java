package com.naima.square_users.services;

import com.naima.square_users.controllers.dto.UserCreationDto;
import com.naima.square_users.dao.entities.UserEntity;
import com.naima.square_users.dao.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService{
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserEntity createUser(UserCreationDto dto) {
        UserEntity entity = new UserEntity(dto.id(),dto.username(),dto.email());
        return userRepository.save(entity);
    }

    @Override
    public Optional<UserEntity> getUserById(String id) {
        return userRepository.findById(id);
    }

    @Override
    public void deleteUser(String id) {
        userRepository.deleteById(id);
    }

    @Override
    public boolean isUserValid(String id) {
        return userRepository.existsById(id);
    }
}
