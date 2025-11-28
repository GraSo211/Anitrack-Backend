package com.graso.anitrack.user.domain.ports;

public interface PasswordEncoderPort {
    String encode(String rawPassword);
   /*  boolean matches(String rawPassword, String encodedPassword); */
}
