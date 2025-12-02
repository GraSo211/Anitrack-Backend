package com.graso.anitrack.anime.infrastructure.persistance;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

/* package com.graso.anitrack.anime.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public class AnimeJpaRepository implements JpaRepository<Anime, Long> {
    
}
 */

@Data
@Entity
@Table(name="users")
public final class AnimeEntity{
    @Id
    private final Long id;
    private final Long malId;
    private final String name;
    
}