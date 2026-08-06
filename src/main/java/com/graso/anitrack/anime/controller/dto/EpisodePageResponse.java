package com.graso.anitrack.anime.controller.dto;

import com.graso.anitrack.anime.model.Episode;
import com.graso.anitrack.anime.model.EpisodePage;

import java.util.List;

public record EpisodePageResponse(
        List<Episode> items,
        int lastPage,
        boolean hasNext
) {
    public static EpisodePageResponse from(EpisodePage page) {
        return new EpisodePageResponse(
                page.items(),
                page.lastPage(),
                page.hasNext()
        );
    }
}
