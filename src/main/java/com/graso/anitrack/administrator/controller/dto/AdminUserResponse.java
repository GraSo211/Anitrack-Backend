package com.graso.anitrack.administrator.controller.dto;

import java.time.LocalDateTime;

public record AdminUserResponse(
        String username,
        LocalDateTime createdAt,
        boolean root
) {

}
