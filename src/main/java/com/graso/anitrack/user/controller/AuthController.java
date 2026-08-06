package com.graso.anitrack.user.controller;

import com.graso.anitrack.config.OAuthStateStore;
import com.graso.anitrack.user.client.myanimelist.dto.ResponseTokenRequest;
import com.graso.anitrack.user.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.Map;

@Controller
@RequestMapping("/api/v1/auth")
public class AuthController {

    AuthService authService;
    String frontendUrl;
    boolean inProduction;

    public AuthController(AuthService authService, @Value("${application.frontend-url}") String frontendUrl, @Value("${application.in-production}") boolean inProduction) {
        this.authService = authService;
        this.frontendUrl = frontendUrl;
        this.inProduction = inProduction;

    }

    @GetMapping("/mal/url")
    public ResponseEntity<Map<String, String>> getUrl() {
        String url = authService.generateAuthorizationUrl();

        return ResponseEntity.ok(
                Map.of("url", url)
        );
    }

    @GetMapping("/mal/login")
    public void loginWithMAL(
            @RequestParam String code,
            @RequestParam String state,
            HttpServletResponse response
    ) throws IOException {
        OAuthStateStore.StateData stateData = authService.consumeState(state);

        if (stateData == null) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "State mismatch - possible CSRF attack");
            return;
        }

        String codeVerifier = stateData.codeVerifier();

        ResponseTokenRequest token =
                authService.loginWithMyAnimeList(code, codeVerifier);

        ResponseCookie.ResponseCookieBuilder accessBuilder =
                ResponseCookie.from("access_token", token.accessToken())
                        .httpOnly(true)
                        .path("/");

        ResponseCookie.ResponseCookieBuilder refreshBuilder =
                ResponseCookie.from("refresh_token", token.refreshToken())
                        .httpOnly(true)
                        .path("/");

        if (inProduction) {
            accessBuilder
                    .secure(true)
                    .sameSite("None")
                    .domain(".anitrack.online");

            refreshBuilder
                    .secure(true)
                    .sameSite("None")
                    .domain(".anitrack.online");
        } else {
            accessBuilder
                    .secure(false)
                    .sameSite("Lax");

            refreshBuilder
                    .secure(false)
                    .sameSite("Lax");
        }

        ResponseCookie accessCookie = accessBuilder
                .maxAge(token.expiresIn())
                .build();

        ResponseCookie refreshCookie = refreshBuilder
                .maxAge(60 * 60 * 24 * 30)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

        response.setStatus(HttpServletResponse.SC_FOUND);
        response.setHeader("Location", frontendUrl + "/perfil");
    }
}
