package com.graso.anitrack.anime.application.port.out;

import com.graso.anitrack.anime.domain.model.Episode;

import java.util.List;

public interface EpisodeQueryPort {
    List<Episode> findAllEpisodesOfAnime(int animeId);
}
