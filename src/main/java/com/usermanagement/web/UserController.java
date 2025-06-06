package com.usermanagement.web;

import com.usermanagement.model.User;
import com.usermanagement.service.UserService;
import com.usermanagement.web.dto.*;
import com.usermanagement.web.mapper.DtoMapper;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping
    public ResponseEntity<List<InfoResponse>> getAllUsers(@RequestParam(required = false) String search) {

        List<User> users = userService.getAllUsers(search);
        List<InfoResponse> userInfoList = users
                .stream()
                .map(DtoMapper::fromUser)
                .toList();

        return ResponseEntity.ok(userInfoList);
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/by-email")
    public ResponseEntity<InfoResponse> getByEmail(@RequestParam String email) {

        User user = userService.getUserByEmail(email);
        InfoResponse infoResponse = DtoMapper.fromUser(user);

        return ResponseEntity.ok(infoResponse);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{userId}")
    public ResponseEntity<Void> updateUser(@RequestBody @Valid UpdateRequest updateRequest,
                                           @PathVariable UUID userId) {

        userService.updateUser(userId, updateRequest);

        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID userId) {

        userService.deleteUser(userId);

        return ResponseEntity.noContent().build();
    }

}
