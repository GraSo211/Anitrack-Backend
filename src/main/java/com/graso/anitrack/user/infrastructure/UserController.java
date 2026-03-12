package com.graso.anitrack.user.infrastructure;


import com.graso.anitrack.user.application.port.in.GetRandomUsersUseCase;
import com.graso.anitrack.user.application.port.in.GetUserByUsernameUseCase;
import com.graso.anitrack.user.domain.UserJikan.RandomUserJikan;
import com.graso.anitrack.user.domain.UserJikan.UserJikan;
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


    private final GetRandomUsersUseCase getRandomUsersUseCase;
    private final GetUserByUsernameUseCase getUserByUsername;


    @GetMapping("/random")
    public ResponseEntity<List<RandomUserJikan>> getRandomUsers(@RequestParam(defaultValue = "5") int count) {
        List<RandomUserJikan> randomUsers = getRandomUsersUseCase.getRandomUsers(count);
        return ResponseEntity.ok(randomUsers);
    }

    @GetMapping("/{username}")
    public ResponseEntity<UserJikan> getUserByUsername(@PathVariable String username) {
        UserJikan user = getUserByUsername.getUserByUsername(username);
        return ResponseEntity.ok(user);
    }

}
