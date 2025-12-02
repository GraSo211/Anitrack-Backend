package com.graso.anitrack.anime.application.port.out;

import java.util.List;

import com.graso.anitrack.anime.domain.model.Anime;

public interface LoadAnimePort {
    List<Anime> findByGenre(String genre);
}
