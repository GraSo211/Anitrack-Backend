package com.graso.anitrack.animelist.client.myanimelist;

import com.graso.anitrack.animelist.client.myanimelist.dto.ResponseAnimeListRequest;
import com.graso.anitrack.animelist.client.myanimelist.dto.ResponseAnimeStatusRequest;
import com.graso.anitrack.animelist.client.myanimelist.dto.ResponseAnimeToListRequest;
import com.graso.anitrack.animelist.client.myanimelist.mapper.MyAnimeListAnimeListMapper;
import com.graso.anitrack.animelist.client.myanimelist.mapper.MyAnimeListStatusMapper;
import com.graso.anitrack.animelist.model.AnimeList;
import com.graso.anitrack.animelist.model.AnimeStatus;
import com.graso.anitrack.importer.client.dto.ResponseAnimeDetailsRequest;
import com.graso.anitrack.importer.client.dto.ResponseAnimeSearchRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URI;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Component
@AllArgsConstructor
public class MyAnimeListAnimeListClient {
    private WebClient myAnimeListWebClientSecondVersion;
    private MyAnimeListAnimeListMapper myAnimeListAnimeListMapper;
    private MyAnimeListStatusMapper myAnimeListStatusMapper;

    public AnimeList getAnimeList(String token, String status) {
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

        return myAnimeListAnimeListMapper.toAnimeList(new ResponseAnimeListRequest(allData, null));
    }

    private String buildAnimeListUri(String status) {
        StringBuilder uri = new StringBuilder("/v2/users/@me/animelist?fields=list_status&limit=1000&nsfw=true");
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

    public AnimeStatus getAnimeStatus(String token, int id) {
        ResponseAnimeStatusRequest response = myAnimeListWebClientSecondVersion.get()
                .uri(String.format("/v2/anime/%d?fields=my_list_status", id))
                .headers(httpHeaders -> httpHeaders.setBearerAuth(token))
                .retrieve()
                .bodyToMono(ResponseAnimeStatusRequest.class)
                .block(Duration.ofSeconds(10));
        return myAnimeListStatusMapper.toAnimeStatus(response);
    }

    public AnimeStatus addAnimeToList(String token, int id) {
        ResponseAnimeToListRequest response = myAnimeListWebClientSecondVersion.patch()
                .uri(String.format("/v2/anime/%d/my_list_status", id))
                .headers(httpHeaders -> httpHeaders.setBearerAuth(token))
                .retrieve()
                .bodyToMono(ResponseAnimeToListRequest.class)
                .block(Duration.ofSeconds(10));
        return myAnimeListStatusMapper.fromAnimeToListToAnimeStatus(response);
    }

    public AnimeStatus modifyAnimeToList(String token, int id, String status, int score, Integer numEpisodes) {
        return patchListStatus(token, id, status, score, numEpisodes);
    }

    public AnimeStatus patchListStatus(String token, int id, String status, int score, Integer numEpisodes) {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("status", status);
        body.add("score", String.valueOf(score));
        if (numEpisodes != null) {
            body.add("num_watched_episodes", String.valueOf(numEpisodes));
        }

        ResponseAnimeToListRequest response = myAnimeListWebClientSecondVersion.patch()
                .uri(String.format("/v2/anime/%d/my_list_status", id))
                .headers(httpHeaders -> httpHeaders.setBearerAuth(token))
                .body(BodyInserters.fromFormData(body))
                .retrieve()
                .bodyToMono(ResponseAnimeToListRequest.class)
                .block(Duration.ofSeconds(10));
        return myAnimeListStatusMapper.fromAnimeToListToAnimeStatus(response);
    }

    public ResponseAnimeDetailsRequest getAnimeDetails(String token, int id) {
        return myAnimeListWebClientSecondVersion.get()
                .uri(String.format(
                        "/v2/anime/%d?fields=id,title,alternative_titles,num_episodes,media_type,start_date,main_picture,related_anime",
                        id))
                .headers(httpHeaders -> httpHeaders.setBearerAuth(token))
                .retrieve()
                .bodyToMono(ResponseAnimeDetailsRequest.class)
                .block(Duration.ofSeconds(10));
    }

    public ResponseAnimeSearchRequest searchAnime(String token, String query) {
        return myAnimeListWebClientSecondVersion.get()
                .uri(uriBuilder -> uriBuilder.path("/v2/anime")
                        .queryParam("q", query)
                        .queryParam("limit", 10)
                        .queryParam("nsfw", true)
                        .queryParam("fields", "id,title,alternative_titles,num_episodes,media_type,start_date,main_picture")
                        .build())
                .headers(httpHeaders -> httpHeaders.setBearerAuth(token))
                .retrieve()
                .bodyToMono(ResponseAnimeSearchRequest.class)
                .block(Duration.ofSeconds(10));
    }
}
