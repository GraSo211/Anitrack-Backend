package com.graso.anitrack.importer.csv;

import java.util.List;

public record CsvParseResult(List<CsvAnimeRow> rows, List<CsvRowError> errors) {
}
