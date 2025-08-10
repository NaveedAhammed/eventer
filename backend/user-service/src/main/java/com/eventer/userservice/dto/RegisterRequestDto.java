package com.eventer.userservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RegisterRequestDto {
    @NotBlank(message = "First Name cannot be null or empty")
    @Size(min = 3, max = 20, message = "The length of user name should be between 3 and 20")
    private String firstName;

    private String lastName;

    @NotBlank(message = "Email cannot be null or empty")
    @Email(message = "Email address should be valid")
    private String email;

    @NotBlank(message = "Password cannot be null or empty")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,20}$",
            message = "Password must be at least 8 characters long, contain an uppercase letter, a lowercase letter, a number, and a special character"
    )
    private String password;

    @NotBlank(message = "Role cannot be null or empty")
    private String role;
}
