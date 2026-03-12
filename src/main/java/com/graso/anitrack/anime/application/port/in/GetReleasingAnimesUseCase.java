package com.graso.anitrack.anime.application.port.in;

import com.graso.anitrack.anime.application.dto.AnimeReleasing;

import java.util.List;

public interface GetReleasingAnimesUseCase {
    List<AnimeReleasing> getReleasingAnimes();

}
