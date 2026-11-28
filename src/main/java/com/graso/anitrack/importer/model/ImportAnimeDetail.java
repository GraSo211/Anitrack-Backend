package com.graso.anitrack.importer.model;

import java.util.List;

public record ImportAnimeDetail(
        int id,
        String title,
        Integer numEpisodes,
        String mediaType,
        String startDate,
        String image,
        List<ImportRelation> relations
) {
}
