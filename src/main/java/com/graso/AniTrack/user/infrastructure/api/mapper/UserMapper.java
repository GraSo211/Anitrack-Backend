package com.graso.anitrack.user.infrastructure.api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import com.graso.anitrack.user.application.command.login.LoginUserRequest;
import com.graso.anitrack.user.application.command.login.LoginUserResponse;
import com.graso.anitrack.user.application.command.register.RegisterUserRequest;
import com.graso.anitrack.user.application.command.register.RegisterUserResponse;
import com.graso.anitrack.user.infrastructure.api.dto.LoginRequestDto;
import com.graso.anitrack.user.infrastructure.api.dto.RegisterRequestDto;
import com.graso.anitrack.user.infrastructure.api.dto.TokenResponseDto;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface UserMapper {
    LoginUserRequest mapToLoginRequest(LoginRequestDto loginRequestDto);

    RegisterUserRequest mapToRegisterRequest(RegisterRequestDto registerRequestDto);

    TokenResponseDto mapToTokenResponseDto(LoginUserResponse loginUserResponse);

    

    TokenResponseDto mapToTokenResponseDto(RegisterUserResponse registerUserResponse);
   
}
