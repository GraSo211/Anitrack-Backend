package com.graso.anitrack.anime.application.service;

import com.graso.anitrack.anime.application.dto.AnimeCard;
import com.graso.anitrack.anime.application.dto.AnimeReleasing;
import com.graso.anitrack.anime.application.dto.AnimeTopSeason;
import com.graso.anitrack.anime.application.dto.EpisodePage;
import com.graso.anitrack.anime.application.port.out.AnimeQueryPort;
import com.graso.anitrack.anime.application.port.out.EpisodeQueryPort;
import com.graso.anitrack.anime.domain.anime.Anime;
import com.graso.anitrack.anime.domain.genre.Genre;
import com.graso.anitrack.anime.domain.genre.Tag;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class AnimeService {
    private final AnimeQueryPort animeQueryPort;
    private final EpisodeQueryPort episodeQueryPort;

    public Anime getById(int id) {
        Anime anime = animeQueryPort.findById(id);
        anime.keepOnlyAnimeRelations();
        return anime;
    }

    public Anime getByMalId(int id) {
        Anime anime = animeQueryPort.findByMalId(id);
        anime.keepOnlyAnimeRelations();
        return anime;
    }

    public Map<String, String> getBanner() {
        return animeQueryPort.getBannerImage();
    }

    public AnimeTopSeason getTopSeasonAnime() {
        List<AnimeTopSeason> candidates = animeQueryPort.findTopSeasonAnime();

        return candidates.stream()
                .filter(c -> c.getBannerImage() != null)
                .sorted(Comparator
                        .comparing(AnimeTopSeason::getAverageScore,
                                Comparator.nullsLast(Comparator.reverseOrder()))
                        .thenComparing(AnimeTopSeason::getPopularity,
                                Comparator.nullsLast(Comparator.reverseOrder())))
                .findFirst()
                .orElse(null);
    }

    public EpisodePage getAllEpisodesOfAnime(int animeId) {
        return episodeQueryPort.findAllEpisodesOfAnime(animeId);
    }

    public List<AnimeReleasing> getReleasingAnimes() {
        return animeQueryPort.findAnimesReleasing();
    }

    public List<AnimeCard> getUpcomingAnimeReleases() {
        return animeQueryPort.findUpcomingAnimeReleases();
    }

    public List<AnimeCard> getSeasonTrendAnimes() {
        return animeQueryPort.findSeasonTrendAnimes();
    }

    public List<AnimeCard> getMostValoratedAnimes() {
        return animeQueryPort.findMostValoratedAnimes();
    }

    public List<Tag> getTags() {
        return animeQueryPort.findAllTags();
    }

    public List<Genre> getGenres() {
        return animeQueryPort.findAllGenres();
    }

    public List<AnimeCard> getFilteredAnimes(int cant, String name, List<String> tags, List<String> genres, int year, String season, String status) {
        return animeQueryPort.findAnimesByFilters(cant, name, tags, genres, year, season, status);
    }
}
