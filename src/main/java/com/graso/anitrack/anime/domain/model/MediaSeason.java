package com.graso.anitrack.anime.domain.model;

import java.time.LocalDate;
import java.time.ZoneId;

public enum MediaSeason {
    WINTER, SPRING, SUMMER, FALL;

    public static MediaSeason current() {
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Tokyo"));
        int year = today.getYear();

        LocalDate springStart = LocalDate.of(year, 3, 20);
        LocalDate summerStart = LocalDate.of(year, 6, 21);
        LocalDate fallStart = LocalDate.of(year, 9, 23);
        LocalDate winterStart = LocalDate.of(year, 12, 21);

        if (today.isBefore(springStart)) {
            return WINTER;
        } else if (today.isBefore(summerStart)) {
            return SPRING;
        } else if (today.isBefore(fallStart)) {
            return SUMMER;
        } else if (today.isBefore(winterStart)) {
            return FALL;
        } else {
            return WINTER;
        }
    }
}
