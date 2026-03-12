package com.graso.anitrack.user.application.service;

import com.graso.anitrack.user.application.port.in.LoginWithMyAnimeListUseCase;
import com.graso.anitrack.user.application.port.out.AuthPort;
import com.graso.anitrack.user.domain.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AuthService implements LoginWithMyAnimeListUseCase {

    private final AuthPort authPort;
    private final String clientId;
    private final String backendUrl;

    public AuthService(
            AuthPort authPort,
            @Value("${mal.client-id}") String clientId,
            @Value("${application.backend-url}") String backendUrl
    ) {
        this.authPort = authPort;
        this.clientId = clientId;
        this.backendUrl = backendUrl;
    }

    @Override
    public String generateAuthorizationUrl() {
        String state = UUID.randomUUID().toString();
        String challenge = UUID.randomUUID().toString();


        return "https://myanimelist.net/v1/oauth2/authorize"
                + "?response_type=code"
                + "&client_id=" + clientId
                + "&redirect_uri=" + backendUrl
                + "&state=" + state
                + "&code_challenge=" + challenge
                + "&code_challenge_method=plain";
    }

    @Override
    public User loginWithMyAnimeList() {
        return authPort.loginUser();
    }
}
