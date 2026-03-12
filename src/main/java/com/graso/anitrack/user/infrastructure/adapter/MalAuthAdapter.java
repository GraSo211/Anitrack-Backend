package com.graso.anitrack.user.infrastructure.adapter;

import com.graso.anitrack.user.application.port.out.AuthPort;
import com.graso.anitrack.user.domain.User;
import org.springframework.stereotype.Component;

@Component
public class MalAuthAdapter implements AuthPort {

    
    @Override
    public User loginUser() {
        return null;
    }
}