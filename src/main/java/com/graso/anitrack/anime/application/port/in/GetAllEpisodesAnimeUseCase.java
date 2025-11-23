package com.graso.anitrack.anime.application.port.in;

import com.graso.anitrack.anime.application.dto.EpisodePage;

public interface GetAllEpisodesAnimeUseCase {
    EpisodePage getAllEpisodesOfAnime(int animeId);
}
