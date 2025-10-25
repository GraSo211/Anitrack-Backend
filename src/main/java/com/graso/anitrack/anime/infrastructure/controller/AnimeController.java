package com.graso.anitrack.anime.infrastructure.controller;

import com.graso.anitrack.anime.application.port.in.GetAnimeByIdUseCase;
import com.graso.anitrack.anime.domain.model.Anime;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/anime")
@AllArgsConstructor
public class AnimeController {

    private final GetAnimeByIdUseCase getAnimeByIdUseCase;
    @GetMapping("/{id}")
    public ResponseEntity<Anime> getAnimeById(@PathVariable Long id){
        Anime anime = getAnimeByIdUseCase.getById(id);
        return new ResponseEntity<>(anime, HttpStatus.OK);
    }
}
