package com.graso.anitrack.anime.model;

import java.util.List;

public record EpisodePage(
        List<Episode> items,
        int lastPage,
        boolean hasNext
) {
}
