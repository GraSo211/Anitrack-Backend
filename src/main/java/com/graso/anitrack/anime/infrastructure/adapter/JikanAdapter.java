package com.graso.anitrack.anime.infrastructure.adapter;

import com.graso.anitrack.anime.application.dto.EpisodePage;
import com.graso.anitrack.anime.application.port.out.EpisodeQueryPort;
import com.graso.anitrack.external.jikan.JikanApiClient;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class JikanAdapter implements EpisodeQueryPort {
    private final JikanApiClient jikanApiClient;

    @Override
    public EpisodePage findAllEpisodesOfAnime(int animeId) {
        return jikanApiClient.fetchAllEpisodesOfAnime(animeId);
    }
}
