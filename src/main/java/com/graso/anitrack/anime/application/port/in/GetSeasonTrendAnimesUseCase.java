package com.graso.anitrack.anime.application.port.in;

import com.graso.anitrack.anime.application.dto.AnimeCard;

import java.util.List;

public interface GetSeasonTrendAnimesUseCase {
    List<AnimeCard> getSeasonTrendAnimes();
}
