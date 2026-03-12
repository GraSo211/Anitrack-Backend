package com.graso.anitrack.anime.application.port.out;

import com.graso.anitrack.anime.application.dto.AnimeCard;
import com.graso.anitrack.anime.application.dto.AnimeReleasing;
import com.graso.anitrack.anime.application.dto.AnimeTopSeason;
import com.graso.anitrack.anime.domain.anime.Anime;
import com.graso.anitrack.anime.domain.genre.Genre;
import com.graso.anitrack.anime.domain.genre.Tag;

import java.util.List;
import java.util.Map;

public interface AnimeQueryPort {
    Anime findById(Long id);

    Map<String, String> getBannerImage();

    AnimeTopSeason findTopSeasonAnime();


    List<AnimeReleasing> findAnimesReleasing();

    List<AnimeCard> findUpcomingAnimeReleases();

    List<AnimeCard> findSeasonTrendAnimes();

    List<AnimeCard> findMostValoratedAnimes();


    List<Genre> findAllGenres();

    List<Tag> findAllTags();

    List<AnimeCard> findAnimesByFilters(int cant, String name, List<String> tags, List<String> genres, int year, String season, String status);
}