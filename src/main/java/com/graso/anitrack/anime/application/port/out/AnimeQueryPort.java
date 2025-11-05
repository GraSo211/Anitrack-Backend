package com.graso.anitrack.anime.application.port.out;

import java.util.List;

import com.graso.anitrack.anime.domain.model.Anime;

public interface AnimeQueryPort {
    Anime findById(Long id);
    Anime findByName(String name);
    List<Anime> findByGenre(String genre);
    List<Anime> findPopular(int limit);
    List<Anime> findUpcoming(int limit);
}