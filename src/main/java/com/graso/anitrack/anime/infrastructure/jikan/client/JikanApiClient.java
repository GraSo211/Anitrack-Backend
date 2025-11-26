package com.graso.anitrack.anime.infrastructure.jikan.client;

import com.graso.anitrack.anime.domain.model.Episode;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Component
@AllArgsConstructor
public class JikanApiClient {
    private WebClient.Builder webClientBuilder;

    public List<Episode> fetchAllEpisodesOfAnime(int animeId) {
        String url = "https://api.jikan.moe/v4/anime/" + animeId + "/episodes";
        return webClientBuilder.build()
                .get()
                .uri(url)
                .retrieve()
                .bodyToFlux(Episode.class)
                .collectList()
                .block();

    }
}
