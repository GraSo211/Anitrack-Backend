package com.graso.anitrack.anime.domain.anime.valueobject;

public record MediaRelation(int relatedMediaId, TypeMediaRelation relationType, MediaType relatedType,
                            String relatedTitle, String relatedImage) {
}
