package com.graso.anitrack.external.myanimelist;

import com.graso.anitrack.external.myanimelist.dto.ResponseTokenRequest;
import com.graso.anitrack.external.myanimelist.dto.ResponseUserRequest;
import com.graso.anitrack.external.myanimelist.mapper.MyAnimeListUserMapper;
import com.graso.anitrack.user.domain.User;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@AllArgsConstructor
public class MyAnimeListApiClient {
    private WebClient myAnimeListWebClientFirstVersion;
    private WebClient myAnimeListWebClientSecondVersion;
    private MyAnimeListUserMapper myAnimeListUserMapper;

    public ResponseTokenRequest getBearerToken(
            String clientId,
            String clientSecret,
            String code,
            String redirectUri,
            String codeVerifier
    ) {

        return myAnimeListWebClientFirstVersion.post()
                .uri("/v1/oauth2/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(
                        BodyInserters
                                .fromFormData("client_id", clientId)
                                .with("client_secret", clientSecret)
                                .with("grant_type", "authorization_code")
                                .with("code", code)
                                .with("redirect_uri", redirectUri)
                                .with("code_verifier", codeVerifier)
                )
                .retrieve()
                .bodyToMono(ResponseTokenRequest.class)
                .block();

    }

    public User getMyUser(String token) {
        ResponseUserRequest responseUserRequest = myAnimeListWebClientSecondVersion.get()
                .uri("/v2/users/@me?fields=anime_statistics")
                .headers(headers -> headers.setBearerAuth(token))
                .retrieve()
                .bodyToMono(ResponseUserRequest.class)
                .block();

        return myAnimeListUserMapper.responseUserRequestToUser(responseUserRequest);
    }


}
