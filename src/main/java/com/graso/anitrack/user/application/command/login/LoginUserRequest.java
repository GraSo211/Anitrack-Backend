package com.graso.anitrack.user.application.command.login;

import com.graso.anitrack.common.application.mediator.Request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginUserRequest implements Request<LoginUserResponse> {
    private String email;
    private String password;

    
}
