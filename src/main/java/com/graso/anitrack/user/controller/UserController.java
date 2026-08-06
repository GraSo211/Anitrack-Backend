package com.graso.anitrack.user.controller;

import com.graso.anitrack.user.controller.dto.RandomUserJikanResponse;
import com.graso.anitrack.user.controller.dto.UserJikanResponse;
import com.graso.anitrack.user.controller.dto.UserResponse;
import com.graso.anitrack.user.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "Users", description = "User API operations")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/random")
    public ResponseEntity<List<RandomUserJikanResponse>> getRandomUsers(@RequestParam(defaultValue = "5") int count) {
        List<RandomUserJikanResponse> dtos = userService.getRandomUsers(count)
                .stream()
                .map(RandomUserJikanResponse::from)
                .toList();
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{username}")
    public ResponseEntity<UserJikanResponse> getUserByUsername(@PathVariable String username) {
        return ResponseEntity.ok(UserJikanResponse.from(userService.getUserByUsername(username)));
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMyUser(@CookieValue("access_token") String token) {
        return ResponseEntity.ok(UserResponse.from(userService.getMyUser(token)));
    }
}
