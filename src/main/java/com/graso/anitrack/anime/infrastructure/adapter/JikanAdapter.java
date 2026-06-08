package com.graso.anitrack.anime.infrastructure.adapter;

import com.graso.anitrack.anime.application.dto.EpisodePage;
import com.graso.anitrack.anime.application.port.out.EpisodeQueryPort;
import com.graso.anitrack.external.jikan.JikanApiClient;
import com.graso.anitrack.external.jikan.dto.ResponseEpisodesJikanDto;
import com.graso.anitrack.external.jikan.mapper.JikanAnimeMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class JikanAdapter implements EpisodeQueryPort {
    private final JikanApiClient jikanApiClient;
    private final JikanAnimeMapper jikanAnimeMapper;

    @Override
    public EpisodePage findAllEpisodesOfAnime(int animeId) {
        ResponseEpisodesJikanDto response = jikanApiClient.fetchAllEpisodesOfAnime(animeId);
        return jikanAnimeMapper.toEpisodePage(response);
    }
}
