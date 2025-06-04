package com.usermanagement.service;

import com.usermanagement.model.User;
import com.usermanagement.repository.UserRepository;
import com.usermanagement.shared.exception.PhoneNumberAlreadyExistException;
import com.usermanagement.shared.exception.UserAlreadyExistsException;
import com.usermanagement.shared.exception.UserNotFoundException;
import com.usermanagement.web.dto.UserCreateRequest;
import com.usermanagement.web.dto.UserUpdateRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void saveUser(UserCreateRequest userCreateRequest) {

        Optional<User> userOptional = userRepository.findByEmail(userCreateRequest.getEmail());

        if (userOptional.isPresent()) {
            throw new UserAlreadyExistsException("User with email [%s] already exists."
                    .formatted(userCreateRequest.getEmail()));
        }

        User user = userRepository.save(initializeUser(userCreateRequest));
        log.info("User saved successfully. [{}]", user);
    }

    public User getUserByEmail(String email) {

        return userRepository.findByEmail(email).orElseThrow(
                () -> new UserNotFoundException("User with email [%s] not found.".formatted(email)));
    }

    public List<User> getAllUsers(String searchName) {

        List<User> users;

        if (searchName != null && !searchName.trim().isEmpty()) {
            users = userRepository.searchUsers(searchName.trim());
        } else {
            users = userRepository.findAllByOrderByLastNameAscDateOfBirthAsc();
        }

        if (users.isEmpty()) {
            throw new UserNotFoundException("No users found.");
        }

        return users;
    }

    public void updateUser(UUID UserId, UserUpdateRequest userUpdateRequest) {

        User user = userRepository.findById(UserId).orElseThrow(
                () -> new UserNotFoundException("User with id [%s] not found.".formatted(UserId)));

        user.setFirstName(userUpdateRequest.getFirstName().trim());
        user.setLastName(userUpdateRequest.getLastName().trim());
        user.setDateOfBirth(userUpdateRequest.getDateOfBirth());

        Optional<User> UserByPhone = userRepository.findByPhoneNumber(userUpdateRequest.getPhoneNumber());
        if (UserByPhone.isPresent() && !UserByPhone.get().getId().equals(user.getId())) {
            throw new PhoneNumberAlreadyExistException("Phone number [%s] already exists."
                    .formatted(userUpdateRequest.getPhoneNumber()));
        }
        user.setPhoneNumber(userUpdateRequest.getPhoneNumber().trim());

        Optional<User> userOptional = userRepository.findByEmail(userUpdateRequest.getEmail());
        if (userOptional.isPresent()) {
            throw new UserAlreadyExistsException("User with email [%s] already exists."
                    .formatted(userUpdateRequest.getEmail()));
        }
        user.setEmail(userUpdateRequest.getEmail().trim());
        user.setPassword(passwordEncoder.encode(userUpdateRequest.getPassword()));

        userRepository.save(user);
    }

    private User initializeUser(UserCreateRequest userCreateRequest) {

        Optional<User> userByPhone = userRepository.findByPhoneNumber(userCreateRequest.getPhoneNumber());
        if (userByPhone.isPresent()) {
            throw new PhoneNumberAlreadyExistException("Phone number [%s] already exists."
                    .formatted(userCreateRequest.getPhoneNumber()));
        }

        return User.builder()
                .firstName(userCreateRequest.getFirstName())
                .lastName(userCreateRequest.getLastName())
                .dateOfBirth(userCreateRequest.getDateOfBirth())
                .phoneNumber(userCreateRequest.getPhoneNumber())
                .email(userCreateRequest.getEmail())
                .password(passwordEncoder.encode(userCreateRequest.getPassword()))
                .createdAt(LocalDateTime.now())
                .build();
    }
}
