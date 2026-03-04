package com.graso.anitrack.anime.infrastructure.jikan.client;

import com.graso.anitrack.anime.application.dto.EpisodePage;
import com.graso.anitrack.anime.infrastructure.jikan.dto.ResponseEpisodesJikanDto;
import com.graso.anitrack.anime.infrastructure.mapper.JikanAnimeMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@AllArgsConstructor
public class JikanApiClient {
    private WebClient.Builder webClientBuilder;
    private JikanAnimeMapper jikanAnimeMapper;

    public EpisodePage fetchAllEpisodesOfAnime(int animeId) {
        String url = STR."https://api.jikan.moe/v4/anime/\{animeId}/episodes";
        ResponseEpisodesJikanDto responseEpisodesJikanDtoMono = webClientBuilder.build()
                .get()
                .uri(url)
                .retrieve()
                .bodyToMono(ResponseEpisodesJikanDto.class)
                .block();

        return jikanAnimeMapper.toEpisodePage(responseEpisodesJikanDtoMono);
    }


}
