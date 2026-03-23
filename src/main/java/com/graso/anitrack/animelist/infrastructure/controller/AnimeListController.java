package com.graso.anitrack.animelist.infrastructure.controller;

import com.graso.anitrack.animelist.application.in.GetAnimeListUseCase;
import com.graso.anitrack.animelist.domain.AnimeList;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/api/v1/animeList")
@AllArgsConstructor
public class AnimeListController {

    GetAnimeListUseCase getAnimeListUseCase;


    @GetMapping("")
    public ResponseEntity<AnimeList> getAnimeList(@CookieValue("access_token") String token, @RequestParam(required = false, defaultValue = "") String status) {
        AnimeList animeList = getAnimeListUseCase.getAnimeListUseCase(token, status);
        return new ResponseEntity<AnimeList>(animeList, HttpStatus.OK);
    }
}
