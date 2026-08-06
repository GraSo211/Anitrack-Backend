package com.graso.anitrack.anime.controller.dto;

import com.graso.anitrack.anime.model.Genre;

public record GenreResponse(String name) {
    public static GenreResponse from(Genre genre) {
        return new GenreResponse(genre.name());
    }
}
