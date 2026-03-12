package com.graso.anitrack.anime.domain.anime.valueobject;

import java.util.List;

public record MediaRelations(
        List<MediaRelation> items
) {
    public MediaRelations {
        items = items != null ? items : List.of();
    }

    public static MediaRelations empty() {
        return new MediaRelations(List.of());
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public MediaRelations onlyAnime() {
        return new MediaRelations(items.stream().filter(i -> i.relatedType() == MediaType.ANIME).toList());
    }
}
