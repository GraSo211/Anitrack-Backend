package com.graso.anitrack.user.controller;

import com.graso.anitrack.administrator.service.AdminUserService;
import com.graso.anitrack.user.controller.dto.UserResponse;
import com.graso.anitrack.user.model.User;
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
    private final AdminUserService adminUserService;

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMyUser(@CookieValue("access_token") String token) {
        User user = userService.getMyUser(token);
        boolean admin = adminUserService.isAdmin(user.getName());

        return ResponseEntity.ok(UserResponse.from(user, admin));
    }
}
