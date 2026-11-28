package com.graso.anitrack.importer.model;

public record SeasonPreview(
        int malId,
        String title,
        int order,
        Integer episodes,
        String image,
        int score,
        boolean selected,
        ExistingStatus existing,
        SeasonAction action,
        String error
) {
}
