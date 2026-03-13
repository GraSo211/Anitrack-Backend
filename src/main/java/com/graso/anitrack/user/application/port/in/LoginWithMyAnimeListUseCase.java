package com.graso.anitrack.user.application.port.in;

import com.graso.anitrack.external.myanimelist.dto.ResponseTokenRequest;
import jakarta.servlet.http.HttpSession;

public interface LoginWithMyAnimeListUseCase {
    String generateAuthorizationUrl(HttpSession session);

    ResponseTokenRequest loginWithMyAnimeList(String code, String state, HttpSession session);
}
