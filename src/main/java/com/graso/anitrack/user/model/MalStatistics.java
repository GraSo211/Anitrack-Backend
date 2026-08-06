package com.graso.anitrack.user.model;

public record MalStatistics(
        Integer numWatching,
        Integer numCompleted,
        Integer numOnHold,
        Integer numDropped,
        Integer numPlanToWatch,
        Integer numTotal,

        Double daysWatched,
        Double daysWatching,
        Double daysCompleted,
        Double daysOnHold,
        Double daysDropped,
        Double daysTotal,

        Integer episodesWatched,
        Integer timesRewatched,
        Integer meanScore
) {
}
