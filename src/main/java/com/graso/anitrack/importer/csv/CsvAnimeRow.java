package com.graso.anitrack.importer.csv;

import java.util.List;

public record CsvAnimeRow(int lineNumber, String title, Integer malId, List<Integer> scores) {
}
