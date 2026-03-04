package com.graso.anitrack.user.domain;

public record Statistics(float daysWatched, float meanScore, int watching, int completed, int onHold, int dropped,
                         int planToWatch, int totalEntries, int rewatched, int episodesWatched) {

}
