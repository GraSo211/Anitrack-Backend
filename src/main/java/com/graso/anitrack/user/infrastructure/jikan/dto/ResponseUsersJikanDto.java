package com.graso.anitrack.user.infrastructure.jikan.dto;

import java.sql.Date;
import java.util.List;

public record ResponseUsersJikanDto(
        Pagination pagination,
        List<Data> data

) {
    public record Pagination(
            int last_visible_page,
            boolean has_next_page
    ) {
    }

    public record Data(
            String username,
            String url,
            Images images,
            Date last_online
    ) {
        public record Images(
                Image jpg,
                Image webp
        ) {
            public record Image(
                    String image_url
            ) {
            }
        }

    }
}
