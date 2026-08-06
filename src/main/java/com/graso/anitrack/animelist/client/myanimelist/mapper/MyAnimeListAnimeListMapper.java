package com.graso.anitrack.animelist.client.myanimelist.mapper;

import com.graso.anitrack.animelist.client.myanimelist.dto.ResponseAnimeListRequest;
import com.graso.anitrack.animelist.model.AnimeItem;
import com.graso.anitrack.animelist.model.AnimeList;
import com.graso.anitrack.animelist.model.Picture;
import com.graso.anitrack.animelist.model.Status;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MyAnimeListAnimeListMapper {

    public AnimeList toAnimeList(ResponseAnimeListRequest response) {

        if (response == null || response.data() == null) {
            throw new RuntimeException("Failed to fetch the anime list");
        }

        List<AnimeItem> items = response.data().stream()
                .map(this::toAnimeItem)
                .toList();

        return new AnimeList(items);
    }

    private AnimeItem toAnimeItem(ResponseAnimeListRequest.Data data) {

        var node = data.node();
        var listStatus = data.listStatus();

        return new AnimeItem(
                node.id(),
                node.title(),
                mapPicture(node.picture()),
                mapStatus(listStatus.status()),
                listStatus.score(),
                listStatus.numWatchedEPisodes(),
                listStatus.isRewatching()
        );
    }

    private Picture mapPicture(ResponseAnimeListRequest.Data.Node.Picture picture) {
        if (picture == null) return null;

        return new Picture(
                picture.large(),
                picture.medium()
        );
    }

    private Status mapStatus(String status) {
        if (status == null) return null;

        return switch (status.toUpperCase()) {
            case "WATCHING" -> Status.WATCHING;
            case "COMPLETED" -> Status.COMPLETED;
            case "ON_HOLD" -> Status.ON_HOLD;
            case "DROPPED" -> Status.DROPPED;
            case "PLAN_TO_WATCH" -> Status.PLAN_TO_WATCH;
            default -> throw new IllegalArgumentException("Unknown status: " + status);
        };
    }
}
