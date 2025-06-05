package com.usermanagement.web;

import com.usermanagement.service.AuthenticationService;
import com.usermanagement.web.dto.CreateRequest;
import com.usermanagement.web.dto.JwtAuthenticationResponse;
import com.usermanagement.web.dto.LoginRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/register")
    public ResponseEntity<JwtAuthenticationResponse> register(@RequestBody @Valid CreateRequest createRequest) {

        JwtAuthenticationResponse jwtAuthenticationResponse = authenticationService.createUser(createRequest);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(jwtAuthenticationResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<JwtAuthenticationResponse> login(@RequestBody @Valid LoginRequest loginRequest) {

        JwtAuthenticationResponse jwtAuthenticationResponse = authenticationService.login(loginRequest);

        return ResponseEntity.ok(jwtAuthenticationResponse);
    }
}
