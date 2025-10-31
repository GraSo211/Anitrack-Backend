package com.graso.anitrack.anime.domain.model;

public record AiringSchedule(int id, int airingAt, int timeUntilAiring, int episode, int mediaId) {
}
