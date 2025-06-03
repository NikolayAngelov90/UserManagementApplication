package com.usermanagement.service;

import com.usermanagement.model.User;
import com.usermanagement.repository.UserRepository;
import com.usermanagement.shared.exception.PhoneNumberAlreadyExistException;
import com.usermanagement.shared.exception.UserAlreadyExistsException;
import com.usermanagement.shared.exception.UserNotFoundException;
import com.usermanagement.web.dto.UserRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void saveUser(UserRequest userRequest) {

        Optional<User> userOptional = userRepository.findByEmail(userRequest.getEmail());

        if (userOptional.isPresent()) {
            throw new UserAlreadyExistsException("User with email [%s] already exists."
                    .formatted(userRequest.getEmail()));
        }

        User user = userRepository.save(initializeUser(userRequest));
        log.info("User saved successfully. [{}]", user);
    }

    public User getUserByEmail(String email) {

        return userRepository.findByEmail(email).orElseThrow(
                () -> new UserNotFoundException("User with email [%s] not found.".formatted(email)));
    }

    public List<User> getAllUsers(String searchTerm) {

        List<User> users;

        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            users = userRepository.searchUsers(searchTerm.trim());
        } else {
            users = userRepository.findAllByOrderByLastNameAscDateOfBirthAsc();
        }

        if (users.isEmpty()) {
            throw new UserNotFoundException("No users found.");
        }

        return users;
    }

    private User initializeUser(UserRequest userRequest) {

        Optional<User> userByPhone = userRepository.findByPhoneNumber(userRequest.getPhoneNumber());
        if (userByPhone.isPresent()) {
            throw new PhoneNumberAlreadyExistException("Phone number [%s] already exists."
                    .formatted(userRequest.getPhoneNumber()));
        }

        return User.builder()
                .firstName(userRequest.getFirstName())
                .lastName(userRequest.getLastName())
                .dateOfBirth(userRequest.getDateOfBirth())
                .phoneNumber(userRequest.getPhoneNumber())
                .email(userRequest.getEmail())
                .password(userRequest.getPassword())
                .createdAt(LocalDateTime.now())
                .build();
    }
}
