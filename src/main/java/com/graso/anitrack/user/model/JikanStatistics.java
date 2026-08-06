package com.graso.anitrack.user.model;

public record JikanStatistics(float daysWatched, float meanScore, int watching, int completed, int onHold, int dropped,
                              int planToWatch, int totalEntries, int rewatched, int episodesWatched) {
}
