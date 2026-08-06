package com.graso.anitrack.animelist.controller.dto;

public record RequestBodyPatchAnimeToList(
        String status,
        int score,
        int numEpisodes
) {
}
