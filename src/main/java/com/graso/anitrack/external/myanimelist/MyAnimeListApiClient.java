package com.graso.anitrack.external.myanimelist;

import com.graso.anitrack.animelist.domain.AnimeList;
import com.graso.anitrack.animelist.domain.AnimeStatus;
import com.graso.anitrack.external.myanimelist.dto.*;
import com.graso.anitrack.external.myanimelist.mapper.MyAnimeListAnimeListMapper;
import com.graso.anitrack.external.myanimelist.mapper.MyAnimeListStatusMapper;
import com.graso.anitrack.external.myanimelist.mapper.MyAnimeListUserMapper;
import com.graso.anitrack.user.domain.User;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@AllArgsConstructor
public class MyAnimeListApiClient {
    private WebClient myAnimeListWebClientFirstVersion;
    private WebClient myAnimeListWebClientSecondVersion;
    private MyAnimeListUserMapper myAnimeListUserMapper;
    private MyAnimeListAnimeListMapper myAnimeListAnimeListMapper;
    private MyAnimeListStatusMapper myAnimeListStatusMapper;

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

    public AnimeList getAnimeList(String token, String status) {
        ResponseAnimeListRequest responseAnimeListRequest = myAnimeListWebClientSecondVersion.get()
                .uri("/v2/users/@me/animelist?status=" + status + "&fields=list_status&limit=1000")
                .headers(httpHeaders -> httpHeaders.setBearerAuth(token))
                .retrieve()
                .bodyToMono(ResponseAnimeListRequest.class)
                .block();
        return myAnimeListAnimeListMapper.toAnimeList(responseAnimeListRequest);
    }

    public AnimeStatus getAnimeStatus(String token, int id) {
        ResponseAnimeStatusRequest responseAnimeStatusRequest = myAnimeListWebClientSecondVersion.get()
                .uri(String.format("v2/anime/%d?fields=my_list_status", id))
                .headers(httpHeaders -> httpHeaders.setBearerAuth(token))
                .retrieve()
                .bodyToMono(ResponseAnimeStatusRequest.class)
                .block();
        return myAnimeListStatusMapper.toAnimeStatus(responseAnimeStatusRequest);
    }

    public AnimeStatus addAnimeToList(String token, int id) {
        ResponseAnimeToListRequest responseAnimeToListRequest = myAnimeListWebClientSecondVersion.patch()
                .uri(String.format("v2/anime/%d/my_list_status", id))
                .headers(httpHeaders -> httpHeaders.setBearerAuth(token))
                .retrieve()
                .bodyToMono(ResponseAnimeToListRequest.class)
                .block();
        return myAnimeListStatusMapper.fromAnimeToListToAnimeStatus(responseAnimeToListRequest);
    }

    public AnimeStatus modifyAnimeToList(String token, int id, MultiValueMap<String, String> body) {
        ResponseAnimeToListRequest responseAnimeToListRequest = myAnimeListWebClientSecondVersion.patch()
                .uri(String.format("v2/anime/%d/my_list_status", id))
                .headers(httpHeaders -> httpHeaders.setBearerAuth(token))
                .body(BodyInserters.fromFormData(body))
                .retrieve()
                .bodyToMono(ResponseAnimeToListRequest.class)
                .block();
        return myAnimeListStatusMapper.fromAnimeToListToAnimeStatus(responseAnimeToListRequest);
    }


}
