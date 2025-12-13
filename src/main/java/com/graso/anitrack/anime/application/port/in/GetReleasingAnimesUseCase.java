package com.graso.anitrack.anime.application.port.in;

import com.graso.anitrack.anime.domain.model.AnimeReleasing;

import java.util.List;

public interface GetReleasingAnimesUseCase {
    List<AnimeReleasing> getReleasingAnimes();

}
