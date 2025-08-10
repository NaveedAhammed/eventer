package com.eventer.userservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginRequestDto {
    @NotBlank(message = "Email cannot be null or empty")
    @Email(message = "Email address should be valid")
    private String email;

    @NotBlank(message = "Password cannot be null or empty")
    private String password;
}
