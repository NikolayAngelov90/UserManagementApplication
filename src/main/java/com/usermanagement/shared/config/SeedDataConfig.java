package com.usermanagement.shared.config;

import com.usermanagement.model.User;
import com.usermanagement.model.UserRole;
import com.usermanagement.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Slf4j
@Component
public class SeedDataConfig implements CommandLineRunner {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public SeedDataConfig(UserService userService,
                          PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {

        if (!userService.checkIfUserExists("admin@admin.com")) {

            User admin = User.builder()
                    .firstName("admin")
                    .lastName("admin")
                    .email("admin@admin.com")
                    .password(passwordEncoder.encode("password"))
                    .role(UserRole.ADMIN)
                    .dateOfBirth(LocalDate.parse("1990-03-29"))
                    .phoneNumber("0000000000")
                    .createdAt(LocalDateTime.now())
                    .build();

            userService.saveUser(admin);
            log.info("Created ADMIN user - {}", admin.getEmail());
        }
    }
}
