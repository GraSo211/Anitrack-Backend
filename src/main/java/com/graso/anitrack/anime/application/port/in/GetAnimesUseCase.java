package com.graso.anitrack.anime.application.port.in;

import java.util.List;

import com.graso.anitrack.anime.domain.model.Media;

public interface GetAnimesUseCase {
    List<Media> get(int quantity, String type, String status, String sort);
}