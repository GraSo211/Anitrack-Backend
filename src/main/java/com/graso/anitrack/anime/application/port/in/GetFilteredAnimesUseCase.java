package com.graso.anitrack.anime.application.port.in;

import com.graso.anitrack.anime.application.dto.AnimeCard;

import java.util.List;

public interface GetFilteredAnimesUseCase {
    List<AnimeCard> getFilteredAnimes(int cant, String name, List<String> tags, List<String> genres, int year, String season, String status);
}
