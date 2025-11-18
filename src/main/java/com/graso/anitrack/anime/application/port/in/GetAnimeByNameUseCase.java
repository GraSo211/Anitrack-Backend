package com.graso.anitrack.anime.application.port.in;

import com.graso.anitrack.anime.domain.model.AnimeName;

import java.util.List;

public interface GetAnimeByNameUseCase {
    public List<AnimeName> getByName(String name);

}
