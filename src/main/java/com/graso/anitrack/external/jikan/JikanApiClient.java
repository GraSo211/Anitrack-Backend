package com.graso.anitrack.external.jikan;

import com.graso.anitrack.external.jikan.dto.ResponseEpisodesJikanDto;
import com.graso.anitrack.external.jikan.dto.ResponseUserByIdJikanDto;
import com.graso.anitrack.external.jikan.dto.ResponseUsersJikanDto;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.Collections;
import java.util.List;

@Component
@AllArgsConstructor
public class JikanApiClient {
    private WebClient jikanWebClient;

    public ResponseUsersJikanDto fetchRandomUsers(int count) {
        return jikanWebClient.get().uri("/users?limit={count}", count)
                .retrieve()
                .bodyToMono(ResponseUsersJikanDto.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(1))
                        .maxBackoff(Duration.ofSeconds(10))
                        .filter(e -> e instanceof WebClientResponseException w &&
                                w.getStatusCode().value() == 429))
                .block(Duration.ofSeconds(10));
    }

    public ResponseUserByIdJikanDto fetchUserByUsername(String username) {
        ResponseUserByIdJikanDto response = jikanWebClient.get().uri("/users/{username}/full", username)
                .retrieve()
                .bodyToMono(ResponseUserByIdJikanDto.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(1))
                        .maxBackoff(Duration.ofSeconds(10))
                        .filter(e -> e instanceof WebClientResponseException w &&
                                w.getStatusCode().value() == 429))
                .block(Duration.ofSeconds(10));

        if (response == null || response.data() == null) {
            throw new RuntimeException("User not found");
        }
        return response;
    }

    public ResponseEpisodesJikanDto fetchAllEpisodesOfAnime(int animeId) {
        return jikanWebClient.get().uri("/anime/{id}/episodes", animeId)
                .retrieve()
                .bodyToMono(ResponseEpisodesJikanDto.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(1))
                        .maxBackoff(Duration.ofSeconds(10))
                        .filter(e -> e instanceof WebClientResponseException w &&
                                w.getStatusCode().value() == 429))
                .block(Duration.ofSeconds(10));
    }
}
