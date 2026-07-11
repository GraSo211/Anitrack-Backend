package com.graso.anitrack.anime.infrastructure.adapter;

import com.graso.anitrack.anime.application.dto.AnimeCard;
import com.graso.anitrack.anime.application.dto.AnimeReleasing;
import com.graso.anitrack.anime.application.dto.AnimeTopSeason;
import com.graso.anitrack.anime.application.port.out.AnimeQueryPort;
import com.graso.anitrack.anime.domain.anime.Anime;
import com.graso.anitrack.anime.domain.genre.Genre;
import com.graso.anitrack.anime.domain.genre.Tag;
import com.graso.anitrack.external.anilist.AniListApiClient;
import com.graso.anitrack.external.anilist.dto.*;
import com.graso.anitrack.external.anilist.mapper.AniListAnimeMapper;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Primary
@Component
@AllArgsConstructor
public class AniListAdapter implements AnimeQueryPort {
    private AniListApiClient aniListApiClient;
    private AniListAnimeMapper aniListAnimeMapper;

    @Override
    public Anime findById(int id) {
        ResponseFetchByIdAniListDto response = aniListApiClient.fetchAnimeById(id);
        return aniListAnimeMapper.toDomain(response);
    }

    @Override
    public Anime findByMalId(int id) {
        ResponseFetchByIdAniListDto response = aniListApiClient.fetchAnimeByMalId(id);
        return aniListAnimeMapper.toDomain(response);
    }

    @Override
    public Map<String, String> getBannerImage() {
        return aniListApiClient.fetchBannerImage();
    }

    @Override
    public List<AnimeTopSeason> findTopSeasonAnime() {
        ResponseTopSeasonAnimeDto response = aniListApiClient.fetchTopAnimeSeason();

        if (response == null || response.data() == null) {
            return List.of();
        }

        List<AnimeTopSeason> candidates = new java.util.ArrayList<>();
        ResponseTopSeasonAnimeDto.Data.AnimeData topScored = response.data().topScored();
        ResponseTopSeasonAnimeDto.Data.AnimeData topPopular = response.data().topPopular();

        if (topScored != null && topScored.media() != null) {
            topScored.media().stream()
                    .map(aniListAnimeMapper::toAnimeTopSeason)
                    .forEach(candidates::add);
        }
        if (topPopular != null && topPopular.media() != null) {
            topPopular.media().stream()
                    .map(aniListAnimeMapper::toAnimeTopSeason)
                    .forEach(candidates::add);
        }
        return candidates;
    }

    @Override
    public List<AnimeReleasing> findAnimesReleasing() {
        List<ResponseReleasingAnimesAniListDto.Data.Page.Media> dtos = aniListApiClient.fetchReleasingAnimes();
        return dtos.stream()
                .map(aniListAnimeMapper::toAnimeReleasing)
                .toList();
    }

    @Override
    public List<AnimeCard> findUpcomingAnimeReleases(int cant) {
        ResponseAnimeCardAniListDto response = aniListApiClient.fetchUpcomingAnimeReleases(cant);

        if (response == null || response.data() == null) {
            return Collections.emptyList();
        }
        return response.data()
                .page()
                .media()
                .stream()
                .map(aniListAnimeMapper::toAnimeCard)
                .toList();
    }

    @Override
    public List<AnimeCard> findSeasonTrendAnimes(int cant) {
        ResponseAnimeCardAniListDto response = aniListApiClient.fetchSeasonTrendAnimes(cant);

        if (response == null || response.data() == null) {
            return Collections.emptyList();
        }
        return response.data()
                .page()
                .media()
                .stream()
                .map(aniListAnimeMapper::toAnimeCard)
                .toList();
    }

    @Override
    public List<AnimeCard> findMostValoratedAnimes(int cant) {
        ResponseAnimeCardAniListDto response = aniListApiClient.fetchMostValoratedAnimes(cant);

        if (response == null || response.data() == null) {
            return Collections.emptyList();
        }
        return response.data()
                .page()
                .media()
                .stream()
                .map(aniListAnimeMapper::toAnimeCard)
                .toList();
    }

    @Override
    public List<Genre> findAllGenres() {
        ResponseGenresAniListDto response = aniListApiClient.fetchAllGenres();

        if (response == null || response.data() == null) {
            return Collections.emptyList();
        }
        return response.data()
                .genreCollection()
                .stream()
                .map(aniListAnimeMapper::toGenre)
                .toList();
    }

    @Override
    public List<Tag> findAllTags() {
        ResponseTagsAniListDto response = aniListApiClient.fetchAllTags();

        if (response == null || response.data() == null) {
            return Collections.emptyList();
        }
        return response.data()
                .mediaTagCollection()
                .stream()
                .map(aniListAnimeMapper::toTag)
                .toList();
    }

    @Override
    public List<AnimeCard> findAnimesByFilters(int cant, String name, List<String> tags, List<String> genres, int year, String season, String status) {
        ResponseAnimeCardAniListDto response = aniListApiClient.fetchAnimesByFilters(cant, name, tags, genres, year, season, status);

        if (response == null || response.data() == null) {
            return Collections.emptyList();
        }
        return response.data()
                .page()
                .media()
                .stream()
                .map(aniListAnimeMapper::toAnimeCard)
                .toList();
    }
}
