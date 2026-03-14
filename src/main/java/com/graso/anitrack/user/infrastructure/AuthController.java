package com.graso.anitrack.user.infrastructure;

import com.graso.anitrack.external.myanimelist.dto.ResponseTokenRequest;
import com.graso.anitrack.user.application.dto.OAuthAuthorization;
import com.graso.anitrack.user.application.port.in.LoginWithMyAnimeListUseCase;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
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

    LoginWithMyAnimeListUseCase loginWithMyAnimeListUseCase;
    String frontendUrl;
    boolean inProduction;

    public AuthController(LoginWithMyAnimeListUseCase loginWithMyAnimeListUseCase, @Value("${application.frontend-url}") String frontendUrl, @Value("${application.in-production}") boolean inProduction) {
        this.loginWithMyAnimeListUseCase = loginWithMyAnimeListUseCase;
        this.frontendUrl = frontendUrl;
        this.inProduction = inProduction;

    }


    @GetMapping("/mal/url")
    public ResponseEntity<Map<String, String>> getUrl() {

        OAuthAuthorization auth = loginWithMyAnimeListUseCase.generateAuthorizationUrl();

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

        String codeVerifier = parts[0];
        String randomState = parts[1];

        ResponseTokenRequest token =
                loginWithMyAnimeListUseCase.loginWithMyAnimeList(code, codeVerifier);

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
