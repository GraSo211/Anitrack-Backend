package com.graso.anitrack.user.infrastructure.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.graso.anitrack.common.application.mediator.Mediator;
import com.graso.anitrack.security.service.JwtService;
import com.graso.anitrack.user.infrastructure.api.dto.LoginRequestDto;
import com.graso.anitrack.user.infrastructure.api.dto.LoginResponseDto;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "Users", description = "User API operations")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final JwtService jwtService;
    private final Mediator mediator;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> loginUser(@RequestBody LoginRequestDto loginRequestDto) {

        return ResponseEntity.ok(null);
    }

    @PostMapping("/register")
    public ResponseEntity<LoginResponseDto> registerUser(@RequestBody LoginRequestDto loginRequestDto) {

        return ResponseEntity.ok(null);
    }

}
