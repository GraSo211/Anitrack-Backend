package com.graso.anitrack.user.application.command.register;

import org.springframework.stereotype.Service;

import com.graso.anitrack.common.application.mediator.RequestHandler;
import com.graso.anitrack.user.domain.Role;
import com.graso.anitrack.user.domain.User;
import com.graso.anitrack.user.domain.ports.AuthenticationPort;
import com.graso.anitrack.user.domain.ports.PasswordEncoderPort;
import com.graso.anitrack.user.domain.ports.UserRepository;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class RegisterUserHandler implements RequestHandler<RegisterUserRequest, RegisterUserResponse> {
    private final UserRepository userRepository;
    private final PasswordEncoderPort passwordEncoder;
    private final AuthenticationPort authenticationPort;
    @Override
    public RegisterUserResponse handle(RegisterUserRequest request) {
        boolean exists = userRepository.existsByEmail(request.getUsername());
        if(exists) {
            throw new IllegalArgumentException("User already exists");
        }
        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();


        User upsert = userRepository.upsert(user);
        String token = authenticationPort.authenticate(upsert);

        return new RegisterUserResponse(token);
    }

    @Override
    public Class<RegisterUserRequest> getRequestType() {
        return RegisterUserRequest.class;
    }
    
}
