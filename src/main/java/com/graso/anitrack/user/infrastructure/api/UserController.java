package com.graso.anitrack.user.infrastructure.api;

import com.graso.anitrack.common.application.mediator.Mediator;
import com.graso.anitrack.user.application.command.login.LoginUserRequest;
import com.graso.anitrack.user.application.command.login.LoginUserResponse;
import com.graso.anitrack.user.application.command.register.RegisterUserRequest;
import com.graso.anitrack.user.application.command.register.RegisterUserResponse;
import com.graso.anitrack.user.application.port.in.GetRandomUsersUseCase;
import com.graso.anitrack.user.application.port.in.GetUserByUsernameUseCase;
import com.graso.anitrack.user.domain.RandomUserJikan;
import com.graso.anitrack.user.domain.UserJikan;
import com.graso.anitrack.user.infrastructure.api.dto.LoginRequestDto;
import com.graso.anitrack.user.infrastructure.api.dto.RegisterRequestDto;
import com.graso.anitrack.user.infrastructure.api.dto.TokenResponseDto;
import com.graso.anitrack.user.infrastructure.api.mapper.UserMapper;
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

    private final Mediator mediator;
    private final UserMapper userMapper;

    private final GetRandomUsersUseCase getRandomUsersUseCase;
    private final GetUserByUsernameUseCase getUserByUsername;

    @PostMapping("/login")
    public ResponseEntity<TokenResponseDto> loginUser(@RequestBody LoginRequestDto loginRequestDto) {
        LoginUserRequest request = userMapper.mapToLoginRequest(loginRequestDto);
        LoginUserResponse response = mediator.dispatch(request);
        TokenResponseDto tokenResponseDto = userMapper.mapToTokenResponseDto(response);
        return ResponseEntity.ok(tokenResponseDto);
    }

    @PostMapping("/register")
    public ResponseEntity<TokenResponseDto> registerUser(@RequestBody RegisterRequestDto registerRequestDto) {
        RegisterUserRequest request = userMapper.mapToRegisterRequest(registerRequestDto);
        RegisterUserResponse response = mediator.dispatch(request);
        TokenResponseDto tokenResponseDto = userMapper.mapToTokenResponseDto(response);
        return ResponseEntity.ok(tokenResponseDto);
    }

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
