package com.graso.anitrack.animelist.infrastructure.controller;

import com.graso.anitrack.animelist.application.in.GetAnimeListStatusUseCase;
import com.graso.anitrack.animelist.application.in.GetAnimeListUseCase;
import com.graso.anitrack.animelist.domain.AnimeList;
import com.graso.anitrack.animelist.domain.AnimeStatus;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/api/v1/animeList")
@AllArgsConstructor
public class AnimeListController {

    GetAnimeListUseCase getAnimeListUseCase;
    GetAnimeListStatusUseCase getAnimeListStatusUseCase;


    @GetMapping("")
    public ResponseEntity<AnimeList> getAnimeList(@CookieValue("access_token") String token, @RequestParam(required = false, defaultValue = "") String status) {
        AnimeList animeList = getAnimeListUseCase.getAnimeListUseCase(token, status);
        return new ResponseEntity<AnimeList>(animeList, HttpStatus.OK);
    }

    @GetMapping("/{id}/status")
    public ResponseEntity<AnimeStatus> getAnimeStatus(@CookieValue("access_token") String token, @PathVariable int id) {
        AnimeStatus animeStatus = getAnimeListStatusUseCase.getAnimeListStatus(token, id);
        return new ResponseEntity<AnimeStatus>(animeStatus, HttpStatus.OK);
    }


}
