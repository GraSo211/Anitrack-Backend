package com.graso.anitrack.user.domain.ports;

public interface AuthenticationPort {
    String authenticate(String username, String password);
}
