package com.graso.anitrack.user.application.command.register;

import com.graso.anitrack.common.application.mediator.Request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RegisterUserRequest implements Request<RegisterUserResponse> {
    private String username;
    private String email;
    private String password;
}
