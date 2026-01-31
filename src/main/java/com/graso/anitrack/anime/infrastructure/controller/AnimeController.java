package com.graso.anitrack.anime.infrastructure.controller;

import com.graso.anitrack.anime.application.dto.EpisodePage;
import com.graso.anitrack.anime.application.port.in.*;
import com.graso.anitrack.anime.domain.model.*;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/anime")
@AllArgsConstructor
public class AnimeController {

    private final GetAnimeByIdUseCase getAnimeByIdUseCase;
    private final GetHomepageBannerAnimeUseCase getHomepageBannerAnimeUseCase;
    private final GetTopSeasonAnimeUseCase getTopSeasonAnimeUseCase;
    private final GetAnimeByNameUseCase getAnimeByNameUseCase;
    private final GetAllEpisodesAnimeUseCase getAllEpisodesAnimeUseCase;
    private final GetReleasingAnimesUseCase getReleasingAnimesUseCase;
    private final GetUpcomingAnimeReleasesUseCase getUpcomingAnimeReleasesUseCase;
    private final GetSeasonTrendAnimesUseCase getSeasonTrendAnimesUseCase;

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

    @GetMapping("/search")
    public ResponseEntity<List<AnimeName>> getAnimeByName(@RequestParam String name) {
        List<AnimeName> animesByName = getAnimeByNameUseCase.getByName(name);
        return new ResponseEntity<>(animesByName, HttpStatus.OK);
    }

    @GetMapping("/{animeId}/episodes")
    public ResponseEntity<EpisodePage> getAllEpisodesOfAnime(@PathVariable int animeId) {
        EpisodePage episodes = getAllEpisodesAnimeUseCase.getAllEpisodesOfAnime(animeId);
        return new ResponseEntity<>(episodes, HttpStatus.OK);
    }

    @GetMapping("/releasingAnimes")
    public ResponseEntity<List<AnimeReleasing>> getReleasingAnimes() {
        List<AnimeReleasing> animesReleasing = getReleasingAnimesUseCase.getReleasingAnimes();
        return new ResponseEntity<>(animesReleasing, HttpStatus.OK);
    }

    @GetMapping("/upcomingAnimeReleases")
    public ResponseEntity<List<AnimeCard>> getUpcomingAnimeReleases() {
        List<AnimeCard> animeCards = getUpcomingAnimeReleasesUseCase.getUpcomingAnimeReleases();
        return new ResponseEntity<>(animeCards, HttpStatus.OK);
    }

    @GetMapping("/seasonTrendAnimes")
    public ResponseEntity<List<AnimeCard>> getSeasonTrendAnimes() {
        List<AnimeCard> animeCards = getSeasonTrendAnimesUseCase.getSeasonTrendAnimes();
        return new ResponseEntity<>(animeCards, HttpStatus.OK);
    }

}
