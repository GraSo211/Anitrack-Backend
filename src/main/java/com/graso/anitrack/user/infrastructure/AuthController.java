package com.graso.anitrack.user.infrastructure;

import com.graso.anitrack.external.myanimelist.dto.ResponseTokenRequest;
import com.graso.anitrack.user.application.dto.OAuthAuthorization;
import com.graso.anitrack.user.application.service.AuthService;
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
import java.nio.charset.StandardCharsets;
import java.util.Base64;
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
        OAuthAuthorization auth = authService.generateAuthorizationUrl();

        return ResponseEntity.ok(
                Map.of("url", auth.authorizationUrl())
        );
    }

    @GetMapping("/mal/login")
    public void loginWithMAL(
            @RequestParam String code,
            @RequestParam String state,
            HttpServletResponse response
    ) throws IOException {
        String decodedState = new String(
                Base64.getUrlDecoder().decode(state),
                StandardCharsets.UTF_8
        );

        String[] parts = decodedState.split(":");

        if (parts.length < 2) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid state parameter");
            return;
        }

        String codeVerifier = parts[0];
        String randomState = parts[1];

        if (!authService.validateState(randomState)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "State mismatch - possible CSRF attack");
            return;
        }

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
