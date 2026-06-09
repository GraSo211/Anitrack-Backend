package com.graso.anitrack.animelist.infrastructure.controller;

import com.graso.anitrack.animelist.application.service.AnimeListService;
import com.graso.anitrack.animelist.domain.AnimeList;
import com.graso.anitrack.animelist.domain.AnimeStatus;
import com.graso.anitrack.animelist.infrastructure.dto.RequestBodyPatchAnimeToList;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/animeList")
@AllArgsConstructor
public class AnimeListController {

    private final AnimeListService animeListService;

    @GetMapping("")
    public ResponseEntity<AnimeList> getAnimeList(@CookieValue("access_token") String token, @RequestParam(required = false) String status) {
        AnimeList animeList = animeListService.getAnimeListUseCase(token, status);
        return ResponseEntity.ok(animeList);
    }

    @GetMapping("/{id}/status")
    public ResponseEntity<AnimeStatus> getAnimeStatus(@CookieValue("access_token") String token, @PathVariable int id) {
        AnimeStatus animeStatus = animeListService.getAnimeListStatus(token, id);
        return ResponseEntity.ok(animeStatus);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<AnimeStatus> postAnimeToList(@CookieValue("access_token") String token, @PathVariable int id) {
        AnimeStatus animeStatus = animeListService.addAnimeToList(token, id);
        return ResponseEntity.ok(animeStatus);
    }

    @PatchMapping("/{id}/update")
    public ResponseEntity<AnimeStatus> patchAnimeToList(@CookieValue("access_token") String token, @PathVariable int id, @RequestBody RequestBodyPatchAnimeToList body) {
        AnimeStatus animeStatus = animeListService.modifyAnimeToList(token, id, body.status(), body.score(), body.numEpisodes());
        return ResponseEntity.ok(animeStatus);
    }

}
