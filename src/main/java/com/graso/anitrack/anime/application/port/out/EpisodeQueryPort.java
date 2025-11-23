package com.graso.anitrack.anime.application.port.out;

import com.graso.anitrack.anime.application.dto.EpisodePage;

public interface EpisodeQueryPort {
    EpisodePage findAllEpisodesOfAnime(int animeId);
}
