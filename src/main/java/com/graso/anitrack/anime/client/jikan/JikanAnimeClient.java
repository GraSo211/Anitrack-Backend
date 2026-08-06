package com.graso.anitrack.anime.client.jikan;

import com.graso.anitrack.anime.client.jikan.dto.ResponseEpisodesJikanDto;
import com.graso.anitrack.anime.client.jikan.mapper.JikanAnimeMapper;
import com.graso.anitrack.anime.model.EpisodePage;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.util.retry.Retry;

import java.time.Duration;

@Component
@AllArgsConstructor
public class JikanAnimeClient {
    private WebClient jikanWebClient;
    private JikanAnimeMapper jikanAnimeMapper;

    public EpisodePage findAllEpisodesOfAnime(int animeId) {
        ResponseEpisodesJikanDto response = jikanWebClient.get()
                .uri("/anime/{id}/episodes", animeId)
                .retrieve()
                .bodyToMono(ResponseEpisodesJikanDto.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(1))
                        .maxBackoff(Duration.ofSeconds(10))
                        .filter(e -> e instanceof WebClientResponseException w &&
                                w.getStatusCode().value() == 429))
                .block(Duration.ofSeconds(10));
        return jikanAnimeMapper.toEpisodePage(response);
    }
}
