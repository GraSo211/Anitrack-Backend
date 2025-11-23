package com.graso.anitrack.anime.infrastructure.jikan.dto;

import java.util.List;

public record ResponseEpisodesJikanDto(
        Pagination pagination,
        List<Data> data
) {
    public record Pagination(
            int last_visible_page,
            boolean has_next_page
    ) {
    }

    public record Data(
            int mal_id,
            String url,
            String title,
            String title_japanese,
            String title_romanji,
            String aired,
            float score,
            boolean filler,
            boolean recap,
            String forum_url

    ) {
    }
}
