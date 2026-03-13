package com.graso.anitrack.external.myanimelist.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ResponseUserRequest(
        int id,
        String name,
        String picture,
        String gender,
        String birthday,
        String location,

        @JsonProperty("joined_at")
        String joinedAt,

        @JsonProperty("anime_statistics")
        AnimeStatistics animeStatistics,

        @JsonProperty("time_zone")
        String timeZone
) {

    public record AnimeStatistics(

            @JsonProperty("num_items_watching")
            Integer numItemsWatching,

            @JsonProperty("num_items_completed")
            Integer numItemsCompleted,

            @JsonProperty("num_items_on_hold")
            Integer numItemsOnHold,

            @JsonProperty("num_items_dropped")
            Integer numItemsDropped,

            @JsonProperty("num_items_plan_to_watch")
            Integer numItemsPlanToWatch,

            @JsonProperty("num_items")
            Integer numItems,

            @JsonProperty("num_days_watched")
            Double numDaysWatched,

            @JsonProperty("num_days_watching")
            Double numDaysWatching,

            @JsonProperty("num_days_completed")
            Double numDaysCompleted,

            @JsonProperty("num_days_on_hold")
            Double numDaysOnHold,

            @JsonProperty("num_days_dropped")
            Double numDaysDropped,

            @JsonProperty("num_days")
            Double numDays,

            @JsonProperty("num_episodes")
            Integer numEpisodes,

            @JsonProperty("num_times_rewatched")
            Integer numTimesRewatched,

            @JsonProperty("mean_score")
            Integer meanScore
    ) {
    }

}