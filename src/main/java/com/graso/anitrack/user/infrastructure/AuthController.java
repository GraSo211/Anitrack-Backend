package com.graso.anitrack.user.infrastructure;

import com.graso.anitrack.external.myanimelist.dto.ResponseTokenRequest;
import com.graso.anitrack.user.application.dto.OAuthAuthorization;
import com.graso.anitrack.user.application.port.in.LoginWithMyAnimeListUseCase;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;

@Controller
@RequestMapping("/api/v1/auth")

public class AuthController {

    LoginWithMyAnimeListUseCase loginWithMyAnimeListUseCase;
    String frontendUrl;
    boolean inProduction;

    public AuthController(LoginWithMyAnimeListUseCase loginWithMyAnimeListUseCase, @Value("${application.frontend-url}") String frontendUrl, @Value("${application.in-production}") boolean inProduction) {
        this.loginWithMyAnimeListUseCase = loginWithMyAnimeListUseCase;
        this.frontendUrl = frontendUrl;
        this.inProduction = inProduction;

    }


    @GetMapping("/mal/url")
    public ResponseEntity<Map<String, String>> getUrl(HttpServletResponse response) {

        OAuthAuthorization auth = loginWithMyAnimeListUseCase.generateAuthorizationUrl();

        ResponseCookie stateCookie = ResponseCookie.from("mal_oauth_state", auth.state())
                .httpOnly(true)
                .secure(inProduction)
                .sameSite(inProduction ? "None" : "Lax")
                .path("/")
                .maxAge(Duration.ofMinutes(5))
                .build();

        ResponseCookie verifierCookie = ResponseCookie.from("mal_code_verifier", auth.codeVerifier())
                .httpOnly(true)
                .secure(inProduction)
                .sameSite(inProduction ? "None" : "Lax")
                .path("/")
                .maxAge(Duration.ofMinutes(5))
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, stateCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, verifierCookie.toString());

        return ResponseEntity.ok(Map.of("url", auth.authorizationUrl()));
    }

    @GetMapping("/mal/login")
    public void loginWithMAL(
            @RequestParam String code,
            @RequestParam String state,
            HttpServletResponse response,
            @CookieValue("mal_oauth_state") String savedState,
            @CookieValue("mal_code_verifier") String codeVerifier
    ) throws IOException {

        ResponseTokenRequest token =
                loginWithMyAnimeListUseCase.loginWithMyAnimeList(code, state, savedState, codeVerifier);

        Cookie accessCookie = new Cookie("access_token", token.accessToken());
        accessCookie.setHttpOnly(true);
        accessCookie.setPath("/");
        accessCookie.setMaxAge(token.expiresIn());
        accessCookie.setSecure(inProduction); // true en producción
        response.addCookie(accessCookie);

        Cookie refreshCookie = new Cookie("refresh_token", token.refreshToken());
        refreshCookie.setHttpOnly(true);
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge(60 * 60 * 24 * 30);
        refreshCookie.setSecure(inProduction); // true en producción
        response.addCookie(refreshCookie);

        response.sendRedirect(frontendUrl + "/perfil");
    }


}
