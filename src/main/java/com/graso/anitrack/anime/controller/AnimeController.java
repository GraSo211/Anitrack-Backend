package com.graso.anitrack.anime.controller;

import com.graso.anitrack.anime.controller.dto.*;
import com.graso.anitrack.anime.service.AnimeService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/anime")
@AllArgsConstructor
@Validated
public class AnimeController {

    private final AnimeService animeService;

    @GetMapping("/{id}")
    public ResponseEntity<AnimeResponse> getAnimeById(@PathVariable int id) {
        return ResponseEntity.ok(AnimeResponse.from(animeService.getById(id)));
    }

    @GetMapping("/mal/{id}")
    public ResponseEntity<AnimeResponse> getAnimeByMalId(@PathVariable int id) {
        return ResponseEntity.ok(AnimeResponse.from(animeService.getByMalId(id)));
    }

    @GetMapping("/bannerImageAnimeOfSeason")
    public ResponseEntity<Map<String, String>> getBannerImageAnimeOfSeason() {
        return ResponseEntity.ok(animeService.getBanner());
    }

    @GetMapping("/topSeasonAnime")
    public ResponseEntity<AnimeTopSeasonResponse> getAnimeTopSeason() {
        return ResponseEntity.ok(AnimeTopSeasonResponse.from(animeService.getTopSeasonAnime()));
    }

    @GetMapping("/releasingAnimes")
    public ResponseEntity<List<AnimeReleasingResponse>> getReleasingAnimes() {
        List<AnimeReleasingResponse> dtos = animeService.getReleasingAnimes()
                .stream()
                .map(AnimeReleasingResponse::from)
                .toList();
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/upcomingAnimeReleases")
    public ResponseEntity<List<AnimeCardResponse>> getUpcomingAnimeReleases(
            @RequestParam(defaultValue = "5") @Min(1) @Max(50) int cant) {
        List<AnimeCardResponse> dtos = animeService.getUpcomingAnimeReleases(cant)
                .stream()
                .map(AnimeCardResponse::from)
                .toList();
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/seasonTrendAnimes")
    public ResponseEntity<List<AnimeCardResponse>> getSeasonTrendAnimes(
            @RequestParam(defaultValue = "5") @Min(1) @Max(50) int cant) {
        List<AnimeCardResponse> dtos = animeService.getSeasonTrendAnimes(cant)
                .stream()
                .map(AnimeCardResponse::from)
                .toList();
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/mostValoratedAnimes")
    public ResponseEntity<List<AnimeCardResponse>> getMostValoratedAnimes(
            @RequestParam(defaultValue = "5") @Min(1) @Max(50) int cant) {
        List<AnimeCardResponse> dtos = animeService.getMostValoratedAnimes(cant)
                .stream()
                .map(AnimeCardResponse::from)
                .toList();
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/allTags")
    public ResponseEntity<List<TagResponse>> getAllTags() {
        List<TagResponse> dtos = animeService.getTags()
                .stream()
                .map(TagResponse::from)
                .toList();
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/allGenres")
    public ResponseEntity<List<GenreResponse>> getAllGenres() {
        List<GenreResponse> dtos = animeService.getGenres()
                .stream()
                .map(GenreResponse::from)
                .toList();
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/filtered")
    public ResponseEntity<List<AnimeCardResponse>> getFilteredAnimes(
            @RequestParam @Min(1) @Max(50) int cant,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) List<String> tag,
            @RequestParam(required = false) List<String> genre,
            @RequestParam(required = false, defaultValue = "0") int year,
            @RequestParam(required = false) String season,
            @RequestParam(required = false) String status) {
        List<AnimeCardResponse> dtos = animeService.getFilteredAnimes(cant, name, tag, genre, year, season, status)
                .stream()
                .map(AnimeCardResponse::from)
                .toList();
        return ResponseEntity.ok(dtos);
    }
}
