package com.graso.anitrack.user.infrastructure;


import com.graso.anitrack.user.application.service.UserService;
import com.graso.anitrack.user.domain.User;
import com.graso.anitrack.user.domain.userjikan.RandomUserJikan;
import com.graso.anitrack.user.domain.userjikan.UserJikan;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "Users", description = "User API operations")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @GetMapping("/random")
    public ResponseEntity<List<RandomUserJikan>> getRandomUsers(@RequestParam(defaultValue = "5") int count) {
        List<RandomUserJikan> randomUsers = userService.getRandomUsers(count);
        return ResponseEntity.ok(randomUsers);
    }

    @GetMapping("/{username}")
    public ResponseEntity<UserJikan> getUserByUsername(@PathVariable String username) {
        UserJikan user = userService.getUserByUsername(username);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/me")
    public ResponseEntity<User> getMyUser(@CookieValue("access_token") String token) {
        System.out.println("llamamos a el get de user controller /me y el token en la cookie es:" + token);

        User user = userService.getMyUser(token);
        return ResponseEntity.ok(user);
    }

}
