package com.graso.anitrack.animelist.controller;

import com.graso.anitrack.animelist.controller.dto.AnimeListResponse;
import com.graso.anitrack.animelist.controller.dto.AnimeStatusResponse;
import com.graso.anitrack.animelist.controller.dto.RequestBodyPatchAnimeToList;
import com.graso.anitrack.animelist.service.AnimeListService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/animeList")
@AllArgsConstructor
public class AnimeListController {

    private final AnimeListService animeListService;

    @GetMapping("")
    public ResponseEntity<AnimeListResponse> getAnimeList(@CookieValue("access_token") String token, @RequestParam(required = false) String status) {
        return ResponseEntity.ok(AnimeListResponse.from(animeListService.getAnimeListUseCase(token, status)));
    }

    @GetMapping("/{id}/status")
    public ResponseEntity<AnimeStatusResponse> getAnimeStatus(@CookieValue("access_token") String token, @PathVariable int id) {
        return ResponseEntity.ok(AnimeStatusResponse.from(animeListService.getAnimeListStatus(token, id)));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<AnimeStatusResponse> postAnimeToList(@CookieValue("access_token") String token, @PathVariable int id) {
        return ResponseEntity.ok(AnimeStatusResponse.from(animeListService.addAnimeToList(token, id)));
    }

    @PatchMapping("/{id}/update")
    public ResponseEntity<AnimeStatusResponse> patchAnimeToList(@CookieValue("access_token") String token, @PathVariable int id, @RequestBody RequestBodyPatchAnimeToList body) {
        return ResponseEntity.ok(AnimeStatusResponse.from(animeListService.modifyAnimeToList(token, id, body.status(), body.score(), body.numEpisodes())));
    }
}
