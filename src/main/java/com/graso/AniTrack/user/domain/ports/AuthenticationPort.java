package com.graso.anitrack.user.domain.ports;

import com.graso.anitrack.user.domain.User;

public interface AuthenticationPort {
    String authenticate(User user);
}
