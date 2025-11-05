package com.graso.anitrack.anime.application.port.in;

import java.util.List;

import com.graso.anitrack.anime.domain.model.Anime;

public interface GetAnimesUseCase {
    List<Anime> get(int quantity, String type, String status, String sort);
}