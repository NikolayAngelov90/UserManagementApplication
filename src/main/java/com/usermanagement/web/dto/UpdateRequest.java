package com.usermanagement.web.dto;

import com.usermanagement.shared.utils.ValidPhoneNumber;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Builder
@Data
public class UpdateRequest {

    @Size(min = 3, max = 20, message = "First name must be between 3 and 20 characters")
    private String firstName;

    @Size(min = 3, max = 20, message = "Last name must be between 3 and 20 characters")
    private String lastName;

    private LocalDate dateOfBirth;

    @ValidPhoneNumber
    private String phoneNumber;

    @Email(message = "Email must be valid")
    private String email;

    @Size(min = 8, max = 20, message = "Password must be between 8 and 20 characters")
    private String password;
}
