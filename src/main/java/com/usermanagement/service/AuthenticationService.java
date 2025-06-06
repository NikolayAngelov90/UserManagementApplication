package com.usermanagement.service;

import com.usermanagement.model.User;
import com.usermanagement.model.UserRole;
import com.usermanagement.shared.exception.PhoneNumberAlreadyExistException;
import com.usermanagement.shared.exception.UserAlreadyExistsException;
import com.usermanagement.web.dto.CreateRequest;
import com.usermanagement.web.dto.JwtAuthenticationResponse;
import com.usermanagement.web.dto.LoginRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
public class AuthenticationService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthenticationService(UserService userService,
                                 PasswordEncoder passwordEncoder,
                                 JwtService jwtService) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public JwtAuthenticationResponse createUser(CreateRequest createRequest) {

        if (userService.checkIfUserExists(createRequest.getEmail())) {
            throw new UserAlreadyExistsException("User with email [%s] already exists."
                    .formatted(createRequest.getEmail()));
        }

        if (userService.checkIfPhoneNumberExists(createRequest.getPhoneNumber())) {
            throw new PhoneNumberAlreadyExistException("Phone number [%s] already exists."
                    .formatted(createRequest.getPhoneNumber()));
        }

        User user = User.builder()
                .firstName(createRequest.getFirstName())
                .lastName(createRequest.getLastName())
                .dateOfBirth(createRequest.getDateOfBirth())
                .phoneNumber(createRequest.getPhoneNumber())
                .email(createRequest.getEmail())
                .password(passwordEncoder.encode(createRequest.getPassword()))
                .role(UserRole.USER)
                .createdAt(LocalDateTime.now())
                .build();

        userService.saveUser(user);
        log.info("User [{}] registered successfully.", user.getEmail());

        UserDetails userDetails = userService.loadUserByUsername(user.getEmail());
        String jwt = jwtService.generateToken(userDetails);

        return JwtAuthenticationResponse.builder().token(jwt).build();
    }

    public JwtAuthenticationResponse login(LoginRequest loginRequest) {

        User user = userService.getUserByEmail(loginRequest.getEmail());

        UserDetails userDetails = userService.loadUserByUsername(user.getEmail());
        String jwt = jwtService.generateToken(userDetails);

        return JwtAuthenticationResponse.builder().token(jwt).build();
    }
}
