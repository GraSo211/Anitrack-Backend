package com.graso.anitrack.user.application.port.in;

import com.graso.anitrack.external.myanimelist.dto.ResponseTokenRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.CookieValue;

public interface LoginWithMyAnimeListUseCase {
    String generateAuthorizationUrl(HttpServletResponse response);

    ResponseTokenRequest loginWithMyAnimeList(String code, String state, @CookieValue("mal_oauth_state") String savedState,
                                              @CookieValue("mal_code_verifier") String codeVerifier);
}
