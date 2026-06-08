package com.graso.anitrack.user.application.service;

import com.graso.anitrack.configuration.OAuthStateStore;
import com.graso.anitrack.external.myanimelist.dto.ResponseTokenRequest;
import com.graso.anitrack.user.application.dto.OAuthAuthorization;
import com.graso.anitrack.user.application.port.out.AuthPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.UUID;

@Service
public class AuthService {

    private final AuthPort authPort;
    private final String clientId;
    private final String backendUrl;
    private final OAuthStateStore stateStore;

    public AuthService(
            AuthPort authPort,
            OAuthStateStore stateStore,
            @Value("${mal.client-id}") String clientId,
            @Value("${application.backend-url}") String backendUrl
    ) {
        this.authPort = authPort;
        this.stateStore = stateStore;
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

    public static String computeCodeChallenge(String codeVerifier) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(codeVerifier.getBytes(StandardCharsets.US_ASCII));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }

    public OAuthAuthorization generateAuthorizationUrl() {
        String randomState = UUID.randomUUID().toString();
        String codeVerifier = generateCodeVerifier();
        String codeChallenge = computeCodeChallenge(codeVerifier);

        String combinedState = codeVerifier + ":" + randomState;

        String encodedState = Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(combinedState.getBytes(StandardCharsets.UTF_8));

        stateStore.store(randomState);

        String url =
                "https://myanimelist.net/v1/oauth2/authorize"
                        + "?response_type=code"
                        + "&client_id=" + clientId
                        + "&redirect_uri=" + backendUrl + "/api/v1/auth/mal/login"
                        + "&state=" + encodedState
                        + "&code_challenge=" + codeChallenge
                        + "&code_challenge_method=S256";

        return new OAuthAuthorization(url);
    }

    public ResponseTokenRequest loginWithMyAnimeList(
            String code,
            String codeVerifier
    ) {
        return authPort.loginUser(code, codeVerifier);
    }

    public boolean validateState(String randomState) {
        return stateStore.consume(randomState);
    }
}
