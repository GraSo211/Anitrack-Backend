package com.graso.anitrack.user.infrastructure.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.graso.anitrack.common.application.mediator.Mediator;
import com.graso.anitrack.user.application.command.login.LoginUserRequest;
import com.graso.anitrack.user.application.command.login.LoginUserResponse;
import com.graso.anitrack.user.application.command.register.RegisterUserRequest;
import com.graso.anitrack.user.application.command.register.RegisterUserResponse;
import com.graso.anitrack.user.infrastructure.api.dto.LoginRequestDto;
import com.graso.anitrack.user.infrastructure.api.dto.RegisterRequestDto;
import com.graso.anitrack.user.infrastructure.api.dto.TokenResponseDto;
import com.graso.anitrack.user.infrastructure.api.mapper.UserMapper;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "Users", description = "User API operations")
@RequiredArgsConstructor
@Slf4j
public class UserController {
 
    private final Mediator mediator;
    private final UserMapper userMapper;
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

}
