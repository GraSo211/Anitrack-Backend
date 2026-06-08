package com.graso.anitrack.external.jikan.dto;

import com.graso.anitrack.user.domain.userjikan.External;

public record ResponseUserByIdJikanDto(Data data) {
    public record Data(
            int mal_id,
            String username,
            String url,
            Images images,
            String last_online,
            String gender,
            String birthday,
            String location,
            String joined,
            Statistics statistics,
            External[] external) {
        public record Images(
                Image jpg,
                Image webp
        ) {
            public record Image(
                    String image_url
            ) {
            }
        }

        public record Statistics(
                StatisticsAnime anime
        ) {
            public record StatisticsAnime(
                    float days_watched,
                    float mean_score,
                    int watching,
                    int completed,
                    int on_hold,
                    int dropped,
                    int plan_to_watch,
                    int total_entries,
                    int rewatched,
                    int episodes_watched
            ) {
            }

        }
    }

}
