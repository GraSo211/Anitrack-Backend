package com.graso.anitrack.user.infrastructure.authentication;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.graso.anitrack.security.service.JwtService;
import com.graso.anitrack.user.domain.ports.AuthenticationPort;
import com.graso.anitrack.user.infrastructure.database.entity.UserEntity;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticactionImpl implements AuthenticationPort {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Override
    public String authenticate(String username, String password) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(username, password)
        );
        UserEntity entity = (UserEntity) authentication.getPrincipal();
        return jwtService.generateToken(entity);
    }

}
