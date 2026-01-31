package com.graso.anitrack.anime.infrastructure.anilist.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.graso.anitrack.anime.domain.model.MediaCoverImage;
import com.graso.anitrack.anime.domain.model.MediaTitle;

import java.util.List;

public record ResponseAnimeCardAniListDto(Data data) {
    public record Data(
            @JsonProperty("Page") Page page
    ) {
        public record Page(List<Media> media) {
            public record Media(
                    int id,
                    Integer idMal,
                    MediaTitle title,
                    MediaCoverImage coverImage
            ) {
            }

        }
    }
}
