package com.wokioki.server.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserRegisterRequest(
        @NotBlank(message = "Username is required") String username,
        @Email(message = "Email should be valid") @NotBlank(message = "Email is required") String email,
        @NotBlank(message = "Password is required") String password
){}
