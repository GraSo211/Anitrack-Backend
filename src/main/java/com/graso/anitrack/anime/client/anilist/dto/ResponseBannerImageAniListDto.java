package com.graso.anitrack.anime.client.anilist.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record ResponseBannerImageAniListDto(
        Data data
) {
    public record Data(
            @JsonProperty("Page")
            Page page
    ) {

        public record Page(
                List<Media> media
        ) {
            public record Media(
                    String bannerImage
            ) {

            }

        }
    }
}
