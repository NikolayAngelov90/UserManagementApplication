package com.usermanagement.web.dto;

import com.usermanagement.shared.utils.ValidPhoneNumber;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
@Data
public class InfoResponse {

    @NotBlank(message = "First name must be provided")
    @Size(min = 3, max = 20, message = "First name must be between 3 and 20 characters")
    private String firstName;

    @NotBlank(message = "Last name must be provided")
    @Size(min = 3, max = 20, message = "Last name must be between 3 and 20 characters")
    private String lastName;

    @NotNull(message = "Date of birth must be provided")
    private LocalDate dateOfBirth;

    @ValidPhoneNumber
    private String phoneNumber;

    @NotBlank(message = "Email must be provided")
    @Email(message = "Email must be valid")
    private String email;

    @NotNull
    private LocalDateTime createdAt;
}
