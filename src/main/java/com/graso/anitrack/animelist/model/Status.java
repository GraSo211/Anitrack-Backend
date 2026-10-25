package com.graso.anitrack.animelist.model;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Locale;

public enum Status {
    WATCHING,
    COMPLETED,
    ON_HOLD,
    DROPPED,
    PLAN_TO_WATCH;

    @JsonValue
    public String wireValue() {
        return name().toLowerCase(Locale.ROOT);
    }
}
