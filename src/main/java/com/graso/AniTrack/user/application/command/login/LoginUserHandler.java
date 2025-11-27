package com.graso.anitrack.user.application.command.login;

import com.graso.anitrack.common.application.mediator.RequestHandler;

public class LoginUserHandler implements RequestHandler<LoginUserRequest, LoginUserResponse> {

    @Override
    public LoginUserResponse handle(LoginUserRequest request) {
        // Implement login logic here
        return null;
    }

    @Override
    public Class<LoginUserRequest> getRequestType() {
        return LoginUserRequest.class;
    }
    
}
