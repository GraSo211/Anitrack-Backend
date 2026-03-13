package com.graso.anitrack.user.application.port.in;

import com.graso.anitrack.external.myanimelist.dto.ResponseTokenRequest;
import com.graso.anitrack.user.application.dto.OAuthAuthorization;

public interface LoginWithMyAnimeListUseCase {

    OAuthAuthorization generateAuthorizationUrl();

    ResponseTokenRequest loginWithMyAnimeList(
            String code,
            String state,
            String savedState,
            String codeVerifier
    );
}
