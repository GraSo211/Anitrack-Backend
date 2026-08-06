package com.graso.anitrack.user.controller.dto;

import com.graso.anitrack.user.model.External;
import com.graso.anitrack.user.model.JikanStatistics;
import com.graso.anitrack.user.model.UserJikan;

import java.util.List;

public record UserJikanResponse(
        int malId,
        String username,
        String url,
        String imageUrl,
        String lastOnline,
        String gender,
        String birthday,
        String location,
        String joined,
        JikanStatistics statistics,
        List<External> external
) {
    public static UserJikanResponse from(UserJikan user) {
        return new UserJikanResponse(
                user.getMalId(),
                user.getUsername(),
                user.getUrl(),
                user.getImageUrl(),
                user.getLastOnline(),
                user.getGender(),
                user.getBirthday(),
                user.getLocation(),
                user.getJoined(),
                user.getStatistics(),
                user.getExternal()
        );
    }
}
