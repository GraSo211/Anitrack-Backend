package com.graso.anitrack.user.controller.dto;

import com.graso.anitrack.user.model.RandomUserJikan;

import java.util.Date;

public record RandomUserJikanResponse(
        String profileUrl,
        String username,
        String imageUrl,
        Date lastOnline
) {
    public static RandomUserJikanResponse from(RandomUserJikan user) {
        return new RandomUserJikanResponse(
                user.profileUrl(),
                user.username(),
                user.imageUrl(),
                user.lastOnline()
        );
    }
}
