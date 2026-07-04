package com.graso.anitrack.user.application.service;

import com.graso.anitrack.configuration.OAuthStateStore;
import com.graso.anitrack.external.myanimelist.dto.ResponseTokenRequest;
import com.graso.anitrack.user.application.port.out.AuthPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.UUID;

@Service
public class AuthService {
    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

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
        this.backendUrl = backendUrl.replaceAll("/+$", "");
    }

    public static String generateCodeVerifier() {
        byte[] code = new byte[64];
        new SecureRandom().nextBytes(code);

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(code);
    }

    public static String computeCodeChallenge(String codeVerifier) {
        return codeVerifier;
    }

    public static String computeCodeChallengeS256(String codeVerifier) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(codeVerifier.getBytes(StandardCharsets.US_ASCII));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }

    public String generateAuthorizationUrl() {
        String state = UUID.randomUUID().toString();
        String codeVerifier = generateCodeVerifier();
        String codeChallenge = computeCodeChallenge(codeVerifier);

        stateStore.store(state, state, codeVerifier);

        String redirectUri = URLEncoder.encode(
                backendUrl + "/api/v1/auth/mal/login", StandardCharsets.UTF_8
        );

        String url =
                "https://myanimelist.net/v1/oauth2/authorize"
                        + "?response_type=code"
                        + "&client_id=" + clientId
                        + "&redirect_uri=" + redirectUri
                        + "&state=" + state
                        + "&code_challenge=" + codeChallenge
                        + "&code_challenge_method=plain";

        log.info("Auth URL: {}", url);
        log.info("codeVerifier: {}, codeChallenge: {}", codeVerifier, codeChallenge);

        return url;
    }

    public ResponseTokenRequest loginWithMyAnimeList(
            String code,
            String codeVerifier
    ) {
        return authPort.loginUser(code, codeVerifier);
    }

    public OAuthStateStore.StateData consumeState(String encodedState) {
        return stateStore.consume(encodedState);
    }
}
