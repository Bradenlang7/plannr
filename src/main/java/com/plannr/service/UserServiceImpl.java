package com.plannr.service;

import com.plannr.dto.BaseUserDTO;
import com.plannr.dto.CreateUserDTO;
import com.plannr.dto.UpdateUserDTO;
import com.plannr.dto.UserMapper;
import com.plannr.entity.User;
import com.plannr.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    @Transactional
    public BaseUserDTO createUser(CreateUserDTO createUserDTO) {
        if (userRepository.existsByUsername(createUserDTO.username())) {
            throw new IllegalStateException("Username already exists");
        }

        User user = userMapper.toUserEntity(createUserDTO);
        //convert to BCrypt password before persisting user to DB
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        user.setPassword(encoder.encode(createUserDTO.password()));

        userRepository.save(user);

        return userMapper.toBaseUserDto(user);
    }

    @Transactional
    @Override
    public BaseUserDTO updateUser(long userId, UpdateUserDTO updateUserDTO) {
        // Retrieve the existing user from the database
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("User not found with id: " + userId));

        // Update only the fields that are provided from the request body
        if (updateUserDTO.username() != null) {
            //If username is being updated - confirm that the name is not already in use.
            if (userRepository.existsByUsername(updateUserDTO.username())) {
                throw new IllegalStateException("Username already exists");
            }
            existingUser.setUsername(updateUserDTO.username());
        }
        if (updateUserDTO.email() != null) {
            existingUser.setEmail(updateUserDTO.email());
        }
        if (updateUserDTO.password() != null) {
            existingUser.setPassword(updateUserDTO.password()); // Hash the password
        }
        if (updateUserDTO.lastname() != null) {
            existingUser.setLastname(updateUserDTO.lastname());
        }
        if (updateUserDTO.firstname() != null) {
            existingUser.setFirstname(updateUserDTO.firstname());
        }

        // Save the updated user and return a DTO
        User updatedUser = userRepository.save(existingUser);
        return userMapper.toBaseUserDto(updatedUser);
    }

    @Override
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with email: " + email));
    }

    @Override
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with username: " + username));
    }

    @Override
    public BaseUserDTO getBaseUserDTOById(long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with id: " + id));

        return userMapper.toBaseUserDto(user);
    }

    @Override
    public User getUserById(long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("User not found with id: " + id));

        return user;
    }

    @Override
    public long deleteUser(long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("User not found with id: " + id));
        userRepository.delete(user);

        return user.getId();
    }

    @Override
    public List<User> findAllUsersByIds(List<Long> ids) {
        return userRepository.findAllById(ids);
    }

}
