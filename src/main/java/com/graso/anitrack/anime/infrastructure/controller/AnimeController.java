package com.graso.anitrack.anime.infrastructure.controller;

import com.graso.anitrack.anime.application.port.in.GetAnimeByIdUseCase;
import com.graso.anitrack.anime.application.port.in.GetHomepageBannerAnimeUseCase;
import com.graso.anitrack.anime.application.port.in.GetTopSeasonAnimeUseCase;
import com.graso.anitrack.anime.domain.model.Anime;
import com.graso.anitrack.anime.domain.model.AnimeTopSeason;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/anime")
@AllArgsConstructor
public class AnimeController {

    private final GetAnimeByIdUseCase getAnimeByIdUseCase;
    private final GetHomepageBannerAnimeUseCase getHomepageBannerAnimeUseCase;
    private final GetTopSeasonAnimeUseCase getTopSeasonAnimeUseCase;


    @GetMapping("/{id}")
    public ResponseEntity<Anime> getAnimeById(@PathVariable Long id) {
        Anime anime = getAnimeByIdUseCase.getById(id);
        return new ResponseEntity<>(anime, HttpStatus.OK);
    }

    @GetMapping("/bannerImageAnimeOfSeason")
    public ResponseEntity<Map<String, String>> getBannerImageAnimeOfSeason() {
        Map<String, String> banner = getHomepageBannerAnimeUseCase.getBanner();
        return new ResponseEntity<>(banner, HttpStatus.OK);
    }

    @GetMapping("/topSeasonAnime")
    public ResponseEntity<AnimeTopSeason> getAnimeTopSeason() {
        AnimeTopSeason animeTopSeason = getTopSeasonAnimeUseCase.getTopSeasonAnime();
        return new ResponseEntity<>(animeTopSeason, HttpStatus.OK);
    }
}
