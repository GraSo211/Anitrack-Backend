package com.graso.anitrack.user.controller;

import com.graso.anitrack.user.controller.dto.UserResponse;
import com.graso.anitrack.user.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "Users", description = "User API operations")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMyUser(@CookieValue("access_token") String token) {
        return ResponseEntity.ok(UserResponse.from(userService.getMyUser(token)));
    }
}
