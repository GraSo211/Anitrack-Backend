package com.graso.anitrack.external.myanimelist;

import com.graso.anitrack.external.myanimelist.dto.*;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@AllArgsConstructor
public class MyAnimeListApiClient {
    private WebClient myAnimeListWebClientFirstVersion;
    private WebClient myAnimeListWebClientSecondVersion;

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
                .block(Duration.ofSeconds(10));
    }

    public ResponseUserRequest getMyUser(String token) {
        return myAnimeListWebClientSecondVersion.get()
                .uri("/v2/users/@me?fields=anime_statistics")
                .headers(headers -> headers.setBearerAuth(token))
                .retrieve()
                .bodyToMono(ResponseUserRequest.class)
                .block(Duration.ofSeconds(10));
    }

    public ResponseAnimeListRequest getAnimeList(String token, String status) {
        String firstUri = buildAnimeListUri(status);
        ResponseAnimeListRequest response = fetchAnimeListPage(token, firstUri);

        List<ResponseAnimeListRequest.Data> allData = new ArrayList<>(response.data());
        String nextUrl = response.paging() != null ? response.paging().next() : null;

        while (nextUrl != null && !nextUrl.isEmpty()) {
            URI nextUri = URI.create(nextUrl);
            String nextPathAndQuery = nextUri.getPath() + "?" + nextUri.getQuery();
            response = fetchAnimeListPage(token, nextPathAndQuery);
            allData.addAll(response.data());
            nextUrl = response.paging() != null ? response.paging().next() : null;
        }

        return new ResponseAnimeListRequest(allData, null);
    }

    private String buildAnimeListUri(String status) {
        StringBuilder uri = new StringBuilder("/v2/users/@me/animelist?fields=list_status&limit=1000");
        if (status != null && !status.isEmpty()) {
            uri.append("&status=").append(status);
        }
        return uri.toString();
    }

    private ResponseAnimeListRequest fetchAnimeListPage(String token, String uri) {
        return myAnimeListWebClientSecondVersion.get()
                .uri(uri)
                .headers(httpHeaders -> httpHeaders.setBearerAuth(token))
                .retrieve()
                .bodyToMono(ResponseAnimeListRequest.class)
                .block(Duration.ofSeconds(10));
    }

    public ResponseAnimeStatusRequest getAnimeStatus(String token, int id) {
        return myAnimeListWebClientSecondVersion.get()
                .uri(String.format("v2/anime/%d?fields=my_list_status", id))
                .headers(httpHeaders -> httpHeaders.setBearerAuth(token))
                .retrieve()
                .bodyToMono(ResponseAnimeStatusRequest.class)
                .block(Duration.ofSeconds(10));
    }

    public ResponseAnimeToListRequest addAnimeToList(String token, int id) {
        return myAnimeListWebClientSecondVersion.patch()
                .uri(String.format("v2/anime/%d/my_list_status", id))
                .headers(httpHeaders -> httpHeaders.setBearerAuth(token))
                .retrieve()
                .bodyToMono(ResponseAnimeToListRequest.class)
                .block(Duration.ofSeconds(10));
    }

    public ResponseAnimeToListRequest modifyAnimeToList(String token, int id, String status, int score, int numEpisodes) {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("status", status);
        body.add("score", String.valueOf(score));
        body.add("num_watched_episodes", String.valueOf(numEpisodes));

        return myAnimeListWebClientSecondVersion.patch()
                .uri(String.format("v2/anime/%d/my_list_status", id))
                .headers(httpHeaders -> httpHeaders.setBearerAuth(token))
                .body(BodyInserters.fromFormData(body))
                .retrieve()
.bodyToMono(ResponseAnimeToListRequest.class)
                .block(Duration.ofSeconds(10));
}
}
