package com.usermanagement.web;

import com.usermanagement.model.User;
import com.usermanagement.service.UserService;
import com.usermanagement.web.dto.UserInfoResponse;
import com.usermanagement.web.dto.UserRequest;
import com.usermanagement.web.mapper.DtoMapper;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/create")
    public ResponseEntity<Void> createUser(@RequestBody @Valid UserRequest userRequest) {

        userService.saveUser(userRequest);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .build();
    }

    @GetMapping("/by-email")
    public ResponseEntity<UserInfoResponse> getByEmail(@RequestParam String email) {

        User user = userService.getUserByEmail(email);
        UserInfoResponse userInfoResponse = DtoMapper.fromUser(user);

        return ResponseEntity.ok(userInfoResponse);
    }

}
