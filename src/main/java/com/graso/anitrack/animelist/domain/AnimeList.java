package com.graso.anitrack.animelist.domain;

import com.graso.anitrack.animelist.domain.valueobject.AnimeItem;

import java.util.List;

public record AnimeList(
        List<AnimeItem> animeList
) {
}