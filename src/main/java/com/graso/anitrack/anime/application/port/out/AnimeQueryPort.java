package com.graso.anitrack.anime.application.port.out;

import java.util.List;

import com.graso.anitrack.anime.domain.model.Media;

public interface AnimeQueryPort {
    Media findById(Long id);
    Media findByName(String name);
    List<Media> findByGenre(String genre);
    List<Media> findPopular(int limit);
    List<Media> findUpcoming(int limit);
}