package com.graso.anitrack.user.application.command.login;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginUserResponse  {
    
    String token;
}
