package com.graso.anitrack.user.infrastructure.api.mapper;


import com.graso.anitrack.user.application.command.login.LoginUserRequest;
import com.graso.anitrack.user.application.command.login.LoginUserResponse;
import com.graso.anitrack.user.application.command.register.RegisterUserRequest;
import com.graso.anitrack.user.application.command.register.RegisterUserResponse;
import com.graso.anitrack.user.infrastructure.api.dto.LoginRequestDto;
import com.graso.anitrack.user.infrastructure.api.dto.RegisterRequestDto;
import com.graso.anitrack.user.infrastructure.api.dto.TokenResponseDto;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public LoginUserRequest mapToLoginRequest(LoginRequestDto loginRequestDto){
        LoginUserRequest loginUserRequest = new LoginUserRequest(loginRequestDto.getEmail(),loginRequestDto.getPassword());
        return loginUserRequest;
    }

    public RegisterUserRequest mapToRegisterRequest(RegisterRequestDto registerRequestDto){
        RegisterUserRequest registerUserRequest = new RegisterUserRequest(registerRequestDto.getUsername(),
                registerRequestDto.getEmail(),registerRequestDto.getPassword());
        return registerUserRequest;
    };

    public TokenResponseDto mapToTokenResponseDto(LoginUserResponse loginUserResponse){
        TokenResponseDto tokenResponseDto = new TokenResponseDto(loginUserResponse.getToken());
        return tokenResponseDto;
    };

    

    public TokenResponseDto mapToTokenResponseDto(RegisterUserResponse registerUserResponse){
        TokenResponseDto tokenResponseDto = new TokenResponseDto(registerUserResponse.getToken());
        return tokenResponseDto;
    };
   
}
