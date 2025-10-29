package com.graso.anitrack.anime.infrastructure.mapper;

import com.graso.anitrack.anime.domain.model.Anime;
import com.graso.anitrack.anime.infrastructure.persistence.entity.AnimeEntity;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AnimePersistenceMapper {
    public AnimeEntity toEntity(Anime anime) {
        AnimeEntity entity = new AnimeEntity();

        entity.setId(anime.id());
        entity.setMalId(anime.malId());
        entity.setName(anime.name());
        entity.setSummary(anime.summary());
        entity.setStatus(anime.status());

        entity.setRating(anime.rating().orElse(null));
        entity.setPopularity(anime.popularity().orElse(null));

        entity.setGenres(anime.genres());

        entity.setImage(anime.image().orElse(null));
        entity.setBannerImage(anime.bannerImage().orElse(null));
        entity.setSource(anime.source().orElse(null));

        entity.setEpisodeCount(anime.episodeCount().orElse(null));
        entity.setAverageEpisodeDuration(anime.averageEpisodeDuration().orElse(null));

        entity.setStartDate(anime.startDate().orElse(null));

        entity.setAdult(anime.adult());

        return entity;
    }


    public Anime toDomain(AnimeEntity entity) {
        return new Anime(
                entity.getId(),
                entity.getMalId(),

                entity.getName(),
                entity.getSummary(),
                entity.getStatus(),

                Optional.ofNullable(entity.getBannerImage()),
                Optional.ofNullable(entity.getImage()),

                entity.getGenres(),

                Optional.ofNullable(entity.getRating()),
                Optional.ofNullable(entity.getPopularity()),

                Optional.ofNullable(entity.getSource()),

                Optional.ofNullable(entity.getEpisodeCount()),
                Optional.ofNullable(entity.getStartDate()),
                Optional.ofNullable(entity.getAverageEpisodeDuration()),

                entity.isAdult()
        );
    }

}
