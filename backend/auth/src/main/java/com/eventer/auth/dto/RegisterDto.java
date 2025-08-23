package com.eventer.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RegisterDto {
    @NotEmpty(message = "Email address is required")
    @Email(message = "Email address is invalid")
    private String email;

    @NotEmpty(message = "Username is required")
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
    @Pattern(
            regexp = "^[A-Za-z0-9]+(?:-[A-Za-z0-9]+)*$",
            message = "Username may contain letters, digits, and single hyphens; it cannot start or end with a hyphen."
    )
    private String username;

    @NotEmpty(message = "Password is required")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!]).{8,}$", message = "Password must be at least 8 characters long, contain one digit, one lowercase, one uppercase, and one special character")
    private String password;

    @NotEmpty(message = "Role is required")
    private String role;
}
