package com.graso.anitrack.anime.application.port.out;

import com.graso.anitrack.anime.domain.model.Anime;

import java.util.Map;

public interface AnimeQueryPort {
    Anime findById(Long id);

    Map<String, String> getBannerImage();

}