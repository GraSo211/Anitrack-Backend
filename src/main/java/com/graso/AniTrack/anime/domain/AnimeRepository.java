package com.graso.anitrack.anime.domain;

import java.util.Optional;

import com.graso.anitrack.anime.domain.model.Anime;

public interface AnimeRepository {
    public Optional<Anime> getAnimeById();
    public Optional<Anime> getAnimeByName();
    
}
 