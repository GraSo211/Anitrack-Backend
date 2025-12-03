package com.graso.anitrack.anime.infrastructure.persistance.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
@Entity
@Table(name="users")
public final class AnimeEntity{
    @Id
    private final Long id;
    private final Long malId;
    @NotNull
    private final String name;
    
}