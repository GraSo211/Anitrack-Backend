package com.graso.anitrack.anime.infrastructure;

import com.graso.anitrack.anime.application.dto.AnimeCard;
import com.graso.anitrack.anime.application.dto.AnimeReleasing;
import com.graso.anitrack.anime.application.dto.AnimeTopSeason;
import com.graso.anitrack.anime.application.dto.EpisodePage;
import com.graso.anitrack.anime.application.service.AnimeService;
import com.graso.anitrack.anime.domain.anime.Anime;
import com.graso.anitrack.anime.domain.genre.Genre;
import com.graso.anitrack.anime.domain.genre.Tag;
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
    public ResponseEntity<Anime> getAnimeById(@PathVariable int id) {
        Anime anime = animeService.getById(id);
        return ResponseEntity.ok(anime);
    }

    @GetMapping("/mal/{id}")
    public ResponseEntity<Anime> getAnimeByMalId(@PathVariable int id) {
        Anime anime = animeService.getByMalId(id);
        return ResponseEntity.ok(anime);
    }

    @GetMapping("/bannerImageAnimeOfSeason")
    public ResponseEntity<Map<String, String>> getBannerImageAnimeOfSeason() {
        Map<String, String> banner = animeService.getBanner();
        return ResponseEntity.ok(banner);
    }

    @GetMapping("/topSeasonAnime")
    public ResponseEntity<AnimeTopSeason> getAnimeTopSeason() {
        AnimeTopSeason animeTopSeason = animeService.getTopSeasonAnime();
        return ResponseEntity.ok(animeTopSeason);
    }

    @GetMapping("/{animeId}/episodes")
    public ResponseEntity<EpisodePage> getAllEpisodesOfAnime(@PathVariable int animeId) {
        EpisodePage episodes = animeService.getAllEpisodesOfAnime(animeId);
        return ResponseEntity.ok(episodes);
    }

    @GetMapping("/releasingAnimes")
    public ResponseEntity<List<AnimeReleasing>> getReleasingAnimes() {
        List<AnimeReleasing> animesReleasing = animeService.getReleasingAnimes();
        return ResponseEntity.ok(animesReleasing);
    }

    @GetMapping("/upcomingAnimeReleases")
    public ResponseEntity<List<AnimeCard>> getUpcomingAnimeReleases(
            @RequestParam(defaultValue = "5") @Min(1) @Max(50) int cant) {
        List<AnimeCard> animeCards = animeService.getUpcomingAnimeReleases(cant);
        return ResponseEntity.ok(animeCards);
    }

    @GetMapping("/seasonTrendAnimes")
    public ResponseEntity<List<AnimeCard>> getSeasonTrendAnimes(
            @RequestParam(defaultValue = "5") @Min(1) @Max(50) int cant) {
        List<AnimeCard> animeCards = animeService.getSeasonTrendAnimes(cant);
        return ResponseEntity.ok(animeCards);
    }

    @GetMapping("/mostValoratedAnimes")
    public ResponseEntity<List<AnimeCard>> getMostValoratedAnimes(
            @RequestParam(defaultValue = "5") @Min(1) @Max(50) int cant) {
        List<AnimeCard> animeCards = animeService.getMostValoratedAnimes(cant);
        return ResponseEntity.ok(animeCards);
    }

    @GetMapping("/allTags")
    public ResponseEntity<List<Tag>> getAllTags() {
        List<Tag> tags = animeService.getTags();
        return ResponseEntity.ok(tags);

    }

    @GetMapping("/allGenres")
    public ResponseEntity<List<Genre>> getAllGenres() {
        List<Genre> genres = animeService.getGenres();
        return ResponseEntity.ok(genres);

    }

    @GetMapping("/filtered")
    public ResponseEntity<List<AnimeCard>> getFilteredAnimes(
            @RequestParam @Min(1) @Max(50) int cant,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) List<String> tag,
            @RequestParam(required = false) List<String> genre,
            @RequestParam(required = false, defaultValue = "0") int year,
            @RequestParam(required = false) String season,
            @RequestParam(required = false) String status) {
        List<AnimeCard> animeCards = animeService.getFilteredAnimes(cant, name, tag, genre, year, season, status);
        return ResponseEntity.ok(animeCards);
    }

}
