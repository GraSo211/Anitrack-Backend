package com.graso.anitrack.anime.application.service;

import com.graso.anitrack.anime.application.dto.EpisodePage;
import com.graso.anitrack.anime.application.port.in.*;
import com.graso.anitrack.anime.application.port.out.AnimeQueryPort;
import com.graso.anitrack.anime.application.port.out.EpisodeQueryPort;
import com.graso.anitrack.anime.domain.model.*;
import com.graso.anitrack.anime.domain.model.genres.Genre;
import com.graso.anitrack.anime.domain.model.genres.Tag;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class AnimeService implements GetAnimeByIdUseCase, GetHomepageBannerAnimeUseCase, GetTopSeasonAnimeUseCase, GetAnimeByNameUseCase, GetAllEpisodesAnimeUseCase, GetReleasingAnimesUseCase, GetUpcomingAnimeReleasesUseCase, GetSeasonTrendAnimesUseCase, GetMostValoratedAnimesUseCase, GetAnimesByGenreUseCase, GetGenresUseCase, GetTagsUseCase, GetFilteredAnimesUseCase {
    AnimeQueryPort animeQueryPort;
    EpisodeQueryPort episodeQueryPort;

    @Override
    public Anime getById(Long id) {

        Anime anime = animeQueryPort.findById(id);
        anime.keepOnlyAnimeRelations();
        return anime;
    }

    @Override
    public Map<String, String> getBanner() {
        return animeQueryPort.getBannerImage();
    }


    @Override
    public AnimeTopSeason getTopSeasonAnime() {
        return animeQueryPort.findTopSeasonAnime();
    }

    @Override
    public List<AnimeName> getByName(String name) {
        return animeQueryPort.findByName(name);
    }

    @Override
    public EpisodePage getAllEpisodesOfAnime(int animeId) {
        return episodeQueryPort.findAllEpisodesOfAnime(animeId);
    }

    @Override
    public List<AnimeReleasing> getReleasingAnimes() {
        return animeQueryPort.findAnimesReleasing();
    }


    @Override
    public List<AnimeCard> getUpcomingAnimeReleases() {
        return animeQueryPort.findUpcomingAnimeReleases();
    }

    @Override
    public List<AnimeCard> getSeasonTrendAnimes() {
        return animeQueryPort.findSeasonTrendAnimes();
    }

    @Override
    public List<AnimeCard> getMostValoratedAnimes() {
        return animeQueryPort.findMostValoratedAnimes();
    }

    @Override
    public List<AnimeCard> getAnimesByGenre(String genre) {
        return animeQueryPort.findAnimesByGenre(genre);
    }

    @Override
    public List<Tag> getTags() {
        return animeQueryPort.findAllTags();
    }

    @Override
    public List<Genre> getGenres() {
        return animeQueryPort.findAllGenres();
    }


    @Override
    public List<AnimeCard> getFilteredAnimes(List<String> tags, List<String> genres, int year, String season, String status) {
        return animeQueryPort.findAnimesByFilters(tags, genres, year, season, status);
    }
}
