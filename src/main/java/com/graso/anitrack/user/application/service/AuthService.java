package com.graso.anitrack.user.application.service;

import com.graso.anitrack.external.myanimelist.dto.ResponseTokenRequest;
import com.graso.anitrack.user.application.dto.OAuthAuthorization;
import com.graso.anitrack.user.application.port.in.LoginWithMyAnimeListUseCase;
import com.graso.anitrack.user.application.port.out.AuthPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;
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

    public static String generateCodeVerifier() {
        byte[] code = new byte[32];
        new SecureRandom().nextBytes(code);

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(code);
    }

    @Override
    public OAuthAuthorization generateAuthorizationUrl() {

        String randomState = UUID.randomUUID().toString();
        String codeVerifier = generateCodeVerifier();

        String combinedState = codeVerifier + ":" + randomState;

        String encodedState = Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(combinedState.getBytes(StandardCharsets.UTF_8));

        String url =
                "https://myanimelist.net/v1/oauth2/authorize"
                        + "?response_type=code"
                        + "&client_id=" + clientId
                        + "&redirect_uri=" + backendUrl + "/api/v1/auth/mal/login"
                        + "&state=" + encodedState
                        + "&code_challenge=" + codeVerifier
                        + "&code_challenge_method=plain";

        return new OAuthAuthorization(url);
    }

    @Override
    public ResponseTokenRequest loginWithMyAnimeList(
            String code,
            String codeVerifier
    ) {
        return authPort.loginUser(code, codeVerifier);
    }
}
