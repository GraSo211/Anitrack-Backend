package com.graso.anitrack.anime.client.jikan.mapper;

import com.graso.anitrack.anime.client.jikan.dto.ResponseEpisodesJikanDto;
import com.graso.anitrack.anime.model.Episode;
import com.graso.anitrack.anime.model.EpisodePage;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class JikanAnimeMapper {

    public EpisodePage toEpisodePage(ResponseEpisodesJikanDto responseEpisodesJikanDto) {
        return new EpisodePage(
                mapEpisodes(responseEpisodesJikanDto.data()),
                responseEpisodesJikanDto.pagination().last_visible_page(),
                responseEpisodesJikanDto.pagination().has_next_page()
        );
    }

    private List<Episode> mapEpisodes(List<ResponseEpisodesJikanDto.Data> data) {
        return data.stream()
                .map(this::mapEpisode)
                .toList();
    }

    private Episode mapEpisode(ResponseEpisodesJikanDto.Data data) {
        return new Episode(
                data.mal_id(),
                data.title(),
                data.filler(),
                data.recap()
        );
    }
}
