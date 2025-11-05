package com.graso.anitrack.anime.domain.model;

public record MediaRelation(int relatedMediaId, TypeMediaRelation relationType, MediaType relatedType,  String relatedTitle, String relatedImage ) {
}
