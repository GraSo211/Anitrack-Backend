package com.graso.anitrack.importer.csv;

public record CsvRowError(int lineNumber, String title, String message) {
}
