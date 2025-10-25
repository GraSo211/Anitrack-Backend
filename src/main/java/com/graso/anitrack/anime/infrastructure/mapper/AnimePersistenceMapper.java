package com.graso.anitrack.anime.infrastructure.mapper;

import com.graso.anitrack.anime.domain.model.Anime;
import com.graso.anitrack.anime.infrastructure.persistence.entity.AnimeEntity;
import org.springframework.stereotype.Component;

@Component
public class AnimePersistenceMapper {

    public AnimeEntity toEntity(Anime anime) {
        AnimeEntity entity = new AnimeEntity();
        entity.setId(anime.id());
        entity.setMalId(anime.malId());
        entity.setName(anime.name());
        entity.setSummary(anime.summary());
        entity.setStatus(anime.status());
        entity.setRating(anime.rating());
        entity.setPopularity(anime.popularity());
        entity.setGenres(anime.genres());
        entity.setImage(anime.image());
        entity.setBannerImage(anime.bannerImage());
        entity.setSource(anime.source());
        entity.setEpisodeCount(anime.episodeCount());
        entity.setStartDate(anime.startDate());
        entity.setAverageEpisodeDuration(anime.averageEpisodeDuration());
        return entity;
    }

    public Anime toDomain(AnimeEntity entity) {
        return new Anime(
                entity.getId(),
                entity.getMalId(),
                entity.getName(),
                entity.getSummary(),
                entity.getStatus(),
                entity.getBannerImage(),
                entity.getRating(),
                entity.getPopularity(),
                entity.getGenres(),
                entity.getImage(),
                entity.getSource(),
                entity.getEpisodeCount(),
                entity.getStartDate(),
                entity.getAverageEpisodeDuration()
        );
    }
}
