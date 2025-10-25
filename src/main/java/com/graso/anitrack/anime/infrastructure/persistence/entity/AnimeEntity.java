package com.graso.anitrack.anime.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "animes")
public class AnimeEntity {

    @Id
    private Long id;

    private Long malId;

    @Column(nullable = false)
    private String name;

    private String summary;

    private String status;

    private int rating;

    private int popularity;

    @ElementCollection
    @CollectionTable(name = "anime_genres", joinColumns = @JoinColumn(name = "anime_id"))
    @Column(name = "genre")
    private List<String> genres;

    private String image;

    private String bannerImage;

    private String source;

    private int episodeCount;

    private LocalDate startDate;

    private int averageEpisodeDuration;
}
