package com.graso.anitrack.external.myanimelist.dto;

public record ResponseAnimeStatusRequest(
        int id,
        String title,
        MainPicture main_picture,
        MyListStatus my_list_status
) {

    public record MainPicture(
            String medium,
            String large
    ) {
    }

    public record MyListStatus(
            String status,
            int score,
            int num_episodes_watched,
            boolean is_rewatching,
            String updated_at
    ) {
    }
}