package com.graso.anitrack.importer.model;

public enum ImportPhase {
    PARSING,
    RESOLVING,
    PREVIEW_READY,
    SYNCING,
    DONE,
    CANCELLED,
    FAILED
}
