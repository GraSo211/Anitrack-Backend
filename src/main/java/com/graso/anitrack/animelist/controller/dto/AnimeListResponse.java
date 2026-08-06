package com.graso.anitrack.animelist.controller.dto;

import com.graso.anitrack.animelist.model.AnimeList;
import com.graso.anitrack.animelist.model.AnimeItem;

import java.util.List;

public record AnimeListResponse(
        List<AnimeItem> animeList
) {
    public static AnimeListResponse from(AnimeList animeList) {
        return new AnimeListResponse(animeList.animeList());
    }
}
