package com.graso.anitrack.external.myanimelist.mapper;

import com.graso.anitrack.animelist.domain.AnimeList;
import com.graso.anitrack.animelist.domain.valueobject.AnimeItem;
import com.graso.anitrack.animelist.domain.valueobject.Picture;
import com.graso.anitrack.animelist.domain.valueobject.Status;
import com.graso.anitrack.external.myanimelist.dto.ResponseAnimeListRequest;
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

        return switch (status.toLowerCase()) {
            case "watching" -> Status.watching;
            case "completed" -> Status.completed;
            case "on_hold" -> Status.on_hold;
            case "dropped" -> Status.dropped;
            case "plan_to_watch" -> Status.plan_to_watch;
            default -> throw new IllegalArgumentException("Unknown status: " + status);
        };
    }
}