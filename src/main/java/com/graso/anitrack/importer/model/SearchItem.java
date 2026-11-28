package com.graso.anitrack.importer.model;

import java.util.List;

public record SearchItem(
        int malId,
        String title,
        List<String> alternativeTitles,
        String mediaType,
        Integer episodes,
        String startDate,
        String image
) {
}
