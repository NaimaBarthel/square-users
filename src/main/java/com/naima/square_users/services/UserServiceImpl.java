package com.naima.square_users.services;

import com.naima.square_users.controllers.dto.UserCreationDto;
import com.naima.square_users.dao.entities.UserEntity;
import com.naima.square_users.dao.repositories.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService{
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserEntity createUser(UserCreationDto dto) {
        // Encodage en BCrypt du mot de passe reçu dans le record
        String encodedPassword = passwordEncoder.encode(dto.password());

        UserEntity entity = new UserEntity();
        entity.setId(null); // Force l'id à null pour déclencher un INSERT propre
        entity.setUsername(dto.username());
        entity.setEmail(dto.email());
        entity.setPassword(encodedPassword);
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
