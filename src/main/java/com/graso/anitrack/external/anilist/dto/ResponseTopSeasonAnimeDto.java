package com.graso.anitrack.external.anilist.dto;

import java.util.List;

public record ResponseTopSeasonAnimeDto(Data data) {
    public record Data(
            AnimeData topScored, AnimeData topPopular
    ) {
        public record AnimeData(List<Media> media) {
            public record Media(int id, Title title, String bannerImage, int meanScore, int popularity) {
                public record Title(String romaji, String english) {
                }
            }
        }
    }
}
