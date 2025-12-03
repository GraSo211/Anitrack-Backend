package com.graso.anitrack.anime.infrastructure.persistance;

import com.graso.anitrack.anime.infrastructure.persistance.entity.AnimeEntity;
import org.mapstruct.Mapper;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface SpringDataAnimeRepository extends JpaRepository<AnimeEntity,Long> {
    Optional<AnimeEntity> findByName(String name);
    
}
