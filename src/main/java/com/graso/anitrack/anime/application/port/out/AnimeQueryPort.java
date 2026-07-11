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
    Anime findById(int id);

    Anime findByMalId(int id);

    Map<String, String> getBannerImage();

    List<AnimeTopSeason> findTopSeasonAnime();


    List<AnimeReleasing> findAnimesReleasing();

    List<AnimeCard> findUpcomingAnimeReleases(int cant);

    List<AnimeCard> findSeasonTrendAnimes(int cant);

    List<AnimeCard> findMostValoratedAnimes(int cant);


    List<Genre> findAllGenres();

    List<Tag> findAllTags();

    List<AnimeCard> findAnimesByFilters(int cant, String name, List<String> tags, List<String> genres, int year, String season, String status);
}