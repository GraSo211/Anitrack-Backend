package com.graso.anitrack.anime.domain.model;

import java.time.LocalDate;

public enum MediaSeason {
    WINTER, SPRING, SUMMER, FALL;

    public static MediaSeason current() {
        int month = LocalDate.now().getMonthValue();

        return switch (month) {
            case 12, 1, 2 -> WINTER;
            case 3, 4, 5 -> SPRING;
            case 6, 7, 8 -> SUMMER;
            case 9, 10, 11 -> FALL;
            default -> throw new IllegalStateException("Invalid month");
        };
    }
}
