package com.graso.anitrack.user.controller.dto;

import com.graso.anitrack.user.model.MalStatistics;
import com.graso.anitrack.user.model.User;

public record UserResponse(
        Integer id,
        String name,
        String url,
        String picture,
        String gender,
        String birthday,
        String location,
        String joinedAt,
        String timeZone,
        MalStatistics statistics
) {
    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getUrl(),
                user.getPicture(),
                user.getGender(),
                user.getBirthday(),
                user.getLocation(),
                user.getJoinedAt(),
                user.getTimeZone(),
                user.getStatistics()
        );
    }
}
