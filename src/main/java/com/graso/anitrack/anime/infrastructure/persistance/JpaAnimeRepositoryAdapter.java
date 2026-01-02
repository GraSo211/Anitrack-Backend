package com.graso.anitrack.anime.infrastructure.persistance;

import java.util.List;
import java.util.Optional;

import com.graso.anitrack.anime.infrastructure.persistance.entity.AnimeEntity;
import com.graso.anitrack.anime.infrastructure.mapper.AnimeEntityMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import com.graso.anitrack.anime.application.port.out.AnimeQueryPort;
import com.graso.anitrack.anime.domain.model.Anime;

@Repository
@AllArgsConstructor
public class JpaAnimeRepositoryAdapter implements AnimeQueryPort {

    private final SpringDataAnimeRepository springDataAnimeRepository;
    private final AnimeEntityMapper animeEntityMapper;

    @Override
    public Anime findById(Long id) {
        Optional<AnimeEntity> animeEntity = springDataAnimeRepository.findById(id);
        if (animeEntity.isPresent()) return animeEntityMapper.mapToAnime(animeEntity.get());
        else throw new RuntimeException("No encontrado") ;
    }

    @Override
    public Anime findByName(String name) {
        Optional<AnimeEntity> animeEntity = springDataAnimeRepository.findByName(name);
        if (animeEntity.isPresent()) return animeEntityMapper.mapToAnime(animeEntity.get());
        else throw new RuntimeException("No encontrado") ;
    }

    @Override
    public List<Anime> findByGenre(String genre) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findByGenre'");
    }

    @Override
    public List<Anime> findPopular(int limit) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findPopular'");
    }

    @Override
    public List<Anime> findUpcoming(int limit) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findUpcoming'");
    }
    
}
