package com.graso.anitrack.user.infrastructure.password;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.graso.anitrack.user.domain.ports.PasswordEncoderPort;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PasswordEncoderImpl implements PasswordEncoderPort {
    private final PasswordEncoder passwordEncoder;
    @Override
    public String encode(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

  /*   @Override
    public boolean matches(String rawPassword, String encodedPassword) {
        // Implement password matching logic here
        return passwordEncoder.matches(rawPassword, encodedPassword);
    } 
     */
}
