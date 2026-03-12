package com.graso.anitrack.anime.application.port.in;

import com.graso.anitrack.anime.domain.genre.Tag;

import java.util.List;

public interface GetTagsUseCase {
    List<Tag> getTags();
}
