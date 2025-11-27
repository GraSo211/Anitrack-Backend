package com.graso.anitrack.user.application.command.login;

import com.graso.anitrack.common.application.mediator.Request;
import lombok.Data;

@Data
public class LoginUserRequest implements Request<LoginUserResponse> {
    private String username;
    private String password;

    
}
