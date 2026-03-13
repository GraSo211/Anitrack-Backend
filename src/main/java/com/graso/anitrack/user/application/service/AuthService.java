package com.graso.anitrack.user.application.service;

import com.graso.anitrack.external.myanimelist.dto.ResponseTokenRequest;
import com.graso.anitrack.user.application.port.in.LoginWithMyAnimeListUseCase;
import com.graso.anitrack.user.application.port.out.AuthPort;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;
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

    public String generateAuthorizationUrl(HttpServletResponse response) {

        String state = UUID.randomUUID().toString();
        String codeVerifier = generateCodeVerifier();

        ResponseCookie stateCookie = ResponseCookie.from("mal_oauth_state", state)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(Duration.ofMinutes(5))
                .sameSite("None")
                .build();

        ResponseCookie verifierCookie = ResponseCookie.from("mal_code_verifier", codeVerifier)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(Duration.ofMinutes(5))
                .sameSite("None")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, stateCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, verifierCookie.toString());

        return "https://myanimelist.net/v1/oauth2/authorize"
                + "?response_type=code"
                + "&client_id=" + clientId
                + "&redirect_uri=" + backendUrl + "/auth/mal/login"
                + "&state=" + state
                + "&code_challenge=" + codeVerifier
                + "&code_challenge_method=plain";
    }

    public ResponseTokenRequest loginWithMyAnimeList(
            String code,
            String state,
            String savedState,
            String codeVerifier
    ) {

        System.out.println("el state que tengo es: " + savedState);
        System.out.println("el state que me dan es: " + state);

        if (!state.equals(savedState)) {
            throw new RuntimeException("Invalid OAuth state");
        }

        ResponseTokenRequest login = authPort.loginUser(code, codeVerifier);

        return login;
    }
}
