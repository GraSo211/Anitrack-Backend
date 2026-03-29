package com.graso.anitrack.anime.infrastructure;

import com.graso.anitrack.anime.application.dto.AnimeCard;
import com.graso.anitrack.anime.application.dto.AnimeReleasing;
import com.graso.anitrack.anime.application.dto.AnimeTopSeason;
import com.graso.anitrack.anime.application.dto.EpisodePage;
import com.graso.anitrack.anime.application.port.in.*;
import com.graso.anitrack.anime.domain.anime.Anime;
import com.graso.anitrack.anime.domain.genre.Genre;
import com.graso.anitrack.anime.domain.genre.Tag;
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
    private final GetAllEpisodesAnimeUseCase getAllEpisodesAnimeUseCase;
    private final GetReleasingAnimesUseCase getReleasingAnimesUseCase;
    private final GetUpcomingAnimeReleasesUseCase getUpcomingAnimeReleasesUseCase;
    private final GetSeasonTrendAnimesUseCase getSeasonTrendAnimesUseCase;
    private final GetMostValoratedAnimesUseCase getMostValoratedAnimesUseCase;
    private final GetTagsUseCase getTagsUseCase;
    private final GetGenresUseCase getGenresUseCase;
    private final GetFilteredAnimesUseCase getFilteredAnimesUseCase;


    @GetMapping("/{id}")
    public ResponseEntity<Anime> getAnimeById(@PathVariable int id) {
        Anime anime = getAnimeByIdUseCase.getById(id);
        return new ResponseEntity<>(anime, HttpStatus.OK);
    }

    @GetMapping("/mal/{id}")
    public ResponseEntity<Anime> getAnimeByMalId(@PathVariable int id) {
        System.out.println("Received request for anime with MAL ID: " + id);
        Anime anime = getAnimeByIdUseCase.getByMalId(id);
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

    @GetMapping("/mostValoratedAnimes")
    public ResponseEntity<List<AnimeCard>> getMostValoratedAnimes() {
        List<AnimeCard> animeCards = getMostValoratedAnimesUseCase.getMostValoratedAnimes();
        return new ResponseEntity<>(animeCards, HttpStatus.OK);
    }


    @GetMapping("/allTags")
    public ResponseEntity<List<Tag>> getAllTags() {
        List<Tag> tags = getTagsUseCase.getTags();
        return new ResponseEntity<>(tags, HttpStatus.OK);

    }

    @GetMapping("/allGenres")
    public ResponseEntity<List<Genre>> getAllGenres() {
        List<Genre> genres = getGenresUseCase.getGenres();
        return new ResponseEntity<>(genres, HttpStatus.OK);

    }

    @GetMapping("/filtered")
    public ResponseEntity<List<AnimeCard>> getFilteredAnimes(
            @RequestParam(required = true) int cant,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) List<String> tag,
            @RequestParam(required = false) List<String> genre,
            @RequestParam(required = false, defaultValue = "0") int year,
            @RequestParam(required = false) String season,
            @RequestParam(required = false) String status) {
        List<AnimeCard> animeCards = getFilteredAnimesUseCase.getFilteredAnimes(cant, name, tag, genre, year, season, status);
        return new ResponseEntity<>(animeCards, HttpStatus.OK);
    }

}
