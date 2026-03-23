package com.graso.anitrack.external.myanimelist.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record ResponseAnimeListRequest(
        List<Data> data,
        Paging paging
) {
    public record Data(
            Node node,
            @JsonProperty("list_status")
            ListStatus listStatus
    ) {
        public record Node(
                int id,
                String title,
                @JsonProperty("main_picture")
                Picture picture
        ) {
            public record Picture(
                    String medium,
                    String large
            ) {

            }
        }

        public record ListStatus(
                String status,
                Integer score,
                @JsonProperty("num_watched_episodes")
                Integer numWatchedEPisodes,
                @JsonProperty("is_rewatching")
                boolean isRewatching
        ) {

        }
    }

    public record Paging(
            String next
    ) {
    }
}
