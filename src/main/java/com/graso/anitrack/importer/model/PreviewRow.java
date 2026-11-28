package com.graso.anitrack.importer.model;

import java.util.List;

public record PreviewRow(
        int rowNumber,
        String title,
        RowStatus status,
        String message,
        List<Candidate> candidates,
        List<SeasonPreview> seasons
) {
}
