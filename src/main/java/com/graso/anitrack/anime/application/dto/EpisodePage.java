package com.graso.anitrack.anime.application.dto;

import com.graso.anitrack.anime.domain.model.Episode;

import java.util.List;

public record EpisodePage(
        List<Episode> items,
        //int currentPage,
        int lastPage,
        boolean hasNext
) {
}
