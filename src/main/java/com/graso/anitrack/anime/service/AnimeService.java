package com.graso.anitrack.anime.service;

import com.graso.anitrack.anime.client.anilist.AniListClient;
import com.graso.anitrack.anime.client.jikan.JikanAnimeClient;
import com.graso.anitrack.anime.model.*;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class AnimeService {
    private final AniListClient aniListClient;
    private final JikanAnimeClient jikanAnimeClient;

    public Anime getById(int id) {
        Anime anime = aniListClient.findById(id);
        anime.keepOnlyAnimeRelations();
        return anime;
    }

    public Anime getByMalId(int id) {
        Anime anime = aniListClient.findByMalId(id);
        anime.keepOnlyAnimeRelations();
        return anime;
    }

    public Map<String, String> getBanner() {
        return aniListClient.fetchBannerImage();
    }

    @Cacheable(value = "topSeasonCache", unless = "#result == null")
    public AnimeTopSeason getTopSeasonAnime() {
        List<AnimeTopSeason> candidates = aniListClient.findTopSeasonAnime();

        return candidates.stream()
                .filter(c -> c.bannerImage() != null)
                .sorted(Comparator
                        .comparing(AnimeTopSeason::averageScore,
                                Comparator.nullsLast(Comparator.reverseOrder()))
                        .thenComparing(AnimeTopSeason::popularity,
                                Comparator.nullsLast(Comparator.reverseOrder())))
                .findFirst()
                .orElse(null);
    }

    public EpisodePage getAllEpisodesOfAnime(int animeId) {
        return jikanAnimeClient.findAllEpisodesOfAnime(animeId);
    }

    public List<AnimeReleasing> getReleasingAnimes() {
        return aniListClient.findReleasingAnimes();
    }

    public List<AnimeCard> getUpcomingAnimeReleases(int cant) {
        return aniListClient.findUpcomingAnimeReleases(cant);
    }

    public List<AnimeCard> getSeasonTrendAnimes(int cant) {
        return aniListClient.findSeasonTrendAnimes(cant);
    }

    public List<AnimeCard> getMostValoratedAnimes(int cant) {
        return aniListClient.findMostValoratedAnimes(cant);
    }

    public List<Tag> getTags() {
        return aniListClient.findAllTags();
    }

    public List<Genre> getGenres() {
        return aniListClient.findAllGenres();
    }

    public List<AnimeCard> getFilteredAnimes(int cant, String name, List<String> tags, List<String> genres, int year, String season, String status) {
        return aniListClient.findAnimesByFilters(cant, name, tags, genres, year, season, status);
    }
}
