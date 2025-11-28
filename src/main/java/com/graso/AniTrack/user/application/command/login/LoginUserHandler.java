package com.graso.anitrack.user.application.command.login;

import org.springframework.stereotype.Service;

import com.graso.anitrack.common.application.mediator.RequestHandler;
import com.graso.anitrack.user.domain.User;
import com.graso.anitrack.user.domain.ports.AuthenticationPort;
import com.graso.anitrack.user.domain.ports.UserRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class LoginUserHandler implements RequestHandler<LoginUserRequest, LoginUserResponse> {
    private final UserRepository userRepository;
    private final AuthenticationPort authenticationPort;

    @Override
    public LoginUserResponse handle(LoginUserRequest request) {

        User user = userRepository.findByEmail(request.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        String token = authenticationPort.authenticate(user);

        return new LoginUserResponse(token);
    }

    @Override
    public Class<LoginUserRequest> getRequestType() {
        return LoginUserRequest.class;
    }

}
