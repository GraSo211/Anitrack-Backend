package com.graso.anitrack.user.infrastructure.api.dto;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class RegisterRequestDto {
    private String username;
    @Email
    private String email;
    private String password;
}
