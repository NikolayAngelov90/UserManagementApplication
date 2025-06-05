package com.usermanagement.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class LoginRequest {

    @NotBlank(message = "Email must be provided")
    @Email(message = "Email must be valid")
    String email;

    @NotBlank(message = "Password must be provided")
    @Size(min = 8, max = 20, message = "Password must be between 8 and 20 characters")
    String password;
}
