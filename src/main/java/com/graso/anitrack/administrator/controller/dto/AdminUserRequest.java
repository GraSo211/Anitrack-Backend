package com.graso.anitrack.administrator.controller.dto;

import jakarta.validation.constraints.NotBlank;

public record AdminUserRequest(
        @NotBlank(message = "El username no puede estar vacío") String username
) {

}
