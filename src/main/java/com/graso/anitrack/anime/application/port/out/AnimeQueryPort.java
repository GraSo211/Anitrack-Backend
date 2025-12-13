package com.graso.anitrack.anime.application.port.out;

import com.graso.anitrack.anime.domain.model.Anime;
import com.graso.anitrack.anime.domain.model.AnimeName;
import com.graso.anitrack.anime.domain.model.AnimeReleasing;
import com.graso.anitrack.anime.domain.model.AnimeTopSeason;

import java.util.List;
import java.util.Map;

public interface AnimeQueryPort {
    Anime findById(Long id);

    Map<String, String> getBannerImage();

    AnimeTopSeason findTopSeasonAnime();

    List<AnimeName> findByName(String name);

    List<AnimeReleasing> findAnimesReleasing();
}