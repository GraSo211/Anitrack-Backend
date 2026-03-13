package com.graso.anitrack.user.application.service;

import com.graso.anitrack.external.myanimelist.dto.ResponseTokenRequest;
import com.graso.anitrack.user.application.port.in.LoginWithMyAnimeListUseCase;
import com.graso.anitrack.user.application.port.out.AuthPort;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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
    public String generateAuthorizationUrl(HttpSession session) {
        String state = UUID.randomUUID().toString();
        String codeVerifier = generateCodeVerifier();

        session.setAttribute("mal_oauth_state", state);
        session.setAttribute("mal_code_verifier", codeVerifier);

        return "https://myanimelist.net/v1/oauth2/authorize"
                + "?response_type=code"
                + "&client_id=" + clientId
                + "&redirect_uri=" + backendUrl + "/auth/mal/login"
                + "&state=" + state
                + "&code_challenge=" + codeVerifier
                + "&code_challenge_method=plain";
    }

    @Override
    public ResponseTokenRequest loginWithMyAnimeList(String code, String state, HttpSession session) {
        String savedState = (String) session.getAttribute("mal_oauth_state");
        System.out.println("el state que tengo es: " + savedState);
        System.out.println("el state que me dan es: " + state);
        if (!state.equals(savedState)) {
            throw new RuntimeException("Invalid OAuth state");
        }

        String codeVerifier = (String) session.getAttribute("mal_code_verifier");
        ResponseTokenRequest login = authPort.loginUser(code, codeVerifier);
        session.removeAttribute("mal_oauth_state");
        session.removeAttribute("mal_code_verifier");
        return login;
    }
}
