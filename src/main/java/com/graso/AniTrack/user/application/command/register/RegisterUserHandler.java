package com.graso.anitrack.user.application.command.register;

import com.graso.anitrack.common.application.mediator.RequestHandler;

public class RegisterUserHandler implements RequestHandler<RegisterUserRequest, RegisterUserResponse> {

    @Override
    public RegisterUserResponse handle(RegisterUserRequest request) {
        // Implement the user registration logic here
        return null;
    }

    @Override
    public Class<RegisterUserRequest> getRequestType() {
        return RegisterUserRequest.class;
    }
    
}
