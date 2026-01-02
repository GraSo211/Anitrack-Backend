package com.graso.anitrack.anime.infrastructure.controller;

import com.graso.anitrack.anime.application.port.in.GetAnimeByIdUseCase;
import lombok.AllArgsConstructor;
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
    public ResponseEntity<Object> getAnimeById(@PathVariable Long id){
        getAnimeByIdUseCase.getById(id);
        return new ResponseEntity<>(null);
    }
}
