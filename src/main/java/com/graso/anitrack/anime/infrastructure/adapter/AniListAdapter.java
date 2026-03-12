package com.graso.anitrack.anime.infrastructure.adapter;

import com.graso.anitrack.anime.application.dto.AnimeCard;
import com.graso.anitrack.anime.application.dto.AnimeReleasing;
import com.graso.anitrack.anime.application.dto.AnimeTopSeason;
import com.graso.anitrack.anime.application.port.out.AnimeQueryPort;
import com.graso.anitrack.anime.domain.anime.Anime;
import com.graso.anitrack.anime.domain.genre.Genre;
import com.graso.anitrack.anime.domain.genre.Tag;
import com.graso.anitrack.external.anilist.AniListApiClient;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Primary
@Component
@AllArgsConstructor
public class AniListAdapter implements AnimeQueryPort {
    private AniListApiClient aniListApiClient;

    @Override
    public Anime findById(Long id) {
        return aniListApiClient.fetchAnimeById(id);
    }

    @Override
    public Map<String, String> getBannerImage() {
        return aniListApiClient.fetchBannerImage();
    }

    @Override
    public AnimeTopSeason findTopSeasonAnime() {
        return aniListApiClient.fetchTopAnimeSeason();
    }


    @Override
    public List<AnimeReleasing> findAnimesReleasing() {
        return aniListApiClient.fetchReleasingAnimes();
    }

    @Override
    public List<AnimeCard> findUpcomingAnimeReleases() {
        return aniListApiClient.fetchUpcomingAnimeReleases();
    }

    @Override
    public List<AnimeCard> findSeasonTrendAnimes() {
        return aniListApiClient.fetchSeasonTrendAnimes();
    }

    @Override
    public List<AnimeCard> findMostValoratedAnimes() {
        return aniListApiClient.fetchMostValoratedAnimes();
    }


    @Override
    public List<Genre> findAllGenres() {
        return aniListApiClient.fetchAllGenres();
    }

    @Override
    public List<Tag> findAllTags() {
        return aniListApiClient.fetchAllTags();
    }

    @Override
    public List<AnimeCard> findAnimesByFilters(int cant, String name, List<String> tags, List<String> genres, int year, String season, String status) {
        return aniListApiClient.fetchAnimesByFilters(cant, name, tags, genres, year, season, status);
    }


}
