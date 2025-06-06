package com.usermanagement.service;

import com.usermanagement.model.User;
import com.usermanagement.repository.UserRepository;
import com.usermanagement.shared.exception.PhoneNumberAlreadyExistException;
import com.usermanagement.shared.exception.UserAlreadyExistsException;
import com.usermanagement.shared.exception.UserNotFoundException;
import com.usermanagement.web.dto.UpdateRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
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

    public void updateUser(UUID UserId, UpdateRequest updateRequest) {

        User user = userRepository.findById(UserId).orElseThrow(
                () -> new UserNotFoundException("User with id [%s] not found.".formatted(UserId)));

        if (updateRequest.getFirstName() != null) {
            user.setFirstName(updateRequest.getFirstName().trim());
        }

        if (updateRequest.getLastName() != null) {
            user.setLastName(updateRequest.getLastName().trim());
        }

        if (updateRequest.getDateOfBirth() != null) {
            user.setDateOfBirth(updateRequest.getDateOfBirth());
        }

        if (updateRequest.getPhoneNumber() != null) {

            Optional<User> UserByPhone = userRepository.findByPhoneNumber(updateRequest.getPhoneNumber());
            if (UserByPhone.isPresent() && !UserByPhone.get().getId().equals(user.getId())) {
                throw new PhoneNumberAlreadyExistException("Phone number [%s] already exists."
                        .formatted(updateRequest.getPhoneNumber()));
            }
            user.setPhoneNumber(updateRequest.getPhoneNumber());
        }

        if (updateRequest.getEmail() != null) {

            Optional<User> userOptional = userRepository.findByEmail(updateRequest.getEmail());
            if (userOptional.isPresent()) {
                throw new UserAlreadyExistsException("User with email [%s] already exists."
                        .formatted(updateRequest.getEmail()));
            }
            user.setEmail(updateRequest.getEmail().trim());
        }

        if (updateRequest.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(updateRequest.getPassword()));
        }

        userRepository.save(user);
        log.info("User [{}] updated successfully.", user.getEmail());
    }

    public void deleteUser(UUID UserId) {

        User user = userRepository.findById(UserId).orElseThrow(
                () -> new UserNotFoundException("User with id [%s] not found.".formatted(UserId)));

        userRepository.delete(user);
        log.info("User [{}] deleted successfully.", user.getEmail());
    }

    public void saveUser(User user) {
        userRepository.save(user);
    }

    public User getUserByEmail(String email) {

        return userRepository.findByEmail(email).orElseThrow(
                () -> new UserNotFoundException("User with email [%s] not found.".formatted(email)));
    }

    public boolean checkIfUserExists(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    public boolean checkIfPhoneNumberExists(String phoneNumber) {
        return userRepository.findByPhoneNumber(phoneNumber).isPresent();
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found."));

        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + user.getRole().name());
        log.info("Attempting to load user by email [{}].", user.getEmail());

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(), user.getPassword(), List.of(authority));
    }
}
