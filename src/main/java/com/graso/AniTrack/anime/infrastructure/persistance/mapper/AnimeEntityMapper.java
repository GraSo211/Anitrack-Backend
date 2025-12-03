package com.graso.anitrack.anime.infrastructure.persistance.mapper;

import com.graso.anitrack.anime.domain.model.Anime;
import com.graso.anitrack.anime.infrastructure.persistance.entity.AnimeEntity;
import com.graso.anitrack.user.infrastructure.database.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface AnimeEntityMapper {
    AnimeEntity mapToAnimeEntity(Anime anime);

    Anime mapToAnime(AnimeEntity animeEntity);
}
