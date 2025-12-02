package com.graso.anitrack.anime.application.port.out;

import java.util.List;
import java.util.Optional;

import com.graso.anitrack.anime.domain.model.Anime;

public interface AnimeQueryPort {
    Optional<Anime> findById(Long id);
    Optional<Anime> findByName(String name);
    List<Anime> findByGenre(String genre);
    List<Anime> findPopular(int limit);
    List<Anime> findUpcoming(int limit);
}