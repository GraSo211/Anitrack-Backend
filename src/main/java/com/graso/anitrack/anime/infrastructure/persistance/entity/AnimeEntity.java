package com.graso.anitrack.anime.infrastructure.persistance.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Date;


@Data
@Entity
@Table(name="animes")
public final class AnimeEntity{
    @Id
    private final Long id;
    private final Long malId;
    @NotNull
    private final String name;
    private String sumary;
    private String status;
    private int calification;
    private int popularity;
    private String[] genres;
    private String image;
    private String banner;
    private String source;
    private int epCount;
    private Date startDate;
    private int avgEpDuration;

    
}