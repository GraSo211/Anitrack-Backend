package com.graso.anitrack.anime.application.port.out;

import com.graso.anitrack.anime.domain.model.*;

import java.util.List;
import java.util.Map;

public interface AnimeQueryPort {
    Anime findById(Long id);

    Map<String, String> getBannerImage();

    AnimeTopSeason findTopSeasonAnime();

    List<AnimeName> findByName(String name);

    List<AnimeReleasing> findAnimesReleasing();

    List<AnimeCard> findUpcomingAnimeReleases();

    List<AnimeCard> findSeasonTrendAnimes();

    List<AnimeCard> findMostValoratedAnimes();

    List<AnimeCard> findAnimesByGenre(String genre);
}