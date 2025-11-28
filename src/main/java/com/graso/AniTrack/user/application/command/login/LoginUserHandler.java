package com.graso.anitrack.user.application.command.login;

import org.springframework.stereotype.Service;

import com.graso.anitrack.common.application.mediator.RequestHandler;
import com.graso.anitrack.user.domain.ports.AuthenticationPort;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class LoginUserHandler implements RequestHandler<LoginUserRequest, LoginUserResponse> {

    private final AuthenticationPort authenticationPort;

    @Override
    public LoginUserResponse handle(LoginUserRequest request) {

        String token = authenticationPort.authenticate(request.getEmail(), request.getPassword());

        return new LoginUserResponse(token);
    }

    @Override
    public Class<LoginUserRequest> getRequestType() {
        return LoginUserRequest.class;
    }

}
