package com.graso.anitrack.importer.csv;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AnimeCsvParser {

    private static final int MAX_DATA_ROWS = 2000;
    private static final BigDecimal MIN_SCORE = BigDecimal.ONE;
    private static final BigDecimal MAX_SCORE = BigDecimal.TEN;

    private static final Set<String> TITLE_ALIASES = Set.of("nombre", "name", "title", "titulo", "anime");
    private static final Set<String> MAL_ID_ALIASES = Set.of("mal_id", "malid", "mal", "id");
    private static final Pattern SCORE_HEADER = Pattern.compile("^(calificacion|score|nota|rating)(?:_(\\d+))?$");

    public CsvParseResult parse(InputStream inputStream) {
        String text = readText(inputStream);
        if (text.isBlank()) {
            throw new IllegalArgumentException("el archivo está vacío");
        }
        char delimiter = detectDelimiter(text);
        List<Record> records = tokenize(text, delimiter);

        int firstIndex = 0;
        while (firstIndex < records.size() && isBlank(records.get(firstIndex))) {
            firstIndex++;
        }
        if (firstIndex == records.size()) {
            throw new IllegalArgumentException("el archivo está vacío");
        }

        boolean hasHeader = TITLE_ALIASES.contains(normalize(cell(records.get(firstIndex), 0)));
        Header header = hasHeader ? headerOf(records.get(firstIndex)) : null;

        List<CsvAnimeRow> rows = new ArrayList<>();
        List<CsvRowError> errors = new ArrayList<>();
        int dataCount = 0;
        for (int i = hasHeader ? firstIndex + 1 : firstIndex; i < records.size(); i++) {
            Record record = records.get(i);
            if (isBlank(record)) {
                continue;
            }
            if (++dataCount > MAX_DATA_ROWS) {
                throw new IllegalArgumentException(
                        "el archivo supera el máximo de " + MAX_DATA_ROWS + " filas de datos");
            }
            try {
                rows.add(header == null ? toHeaderlessRow(record) : toRow(record, header));
            } catch (RowProblem problem) {
                errors.add(new CsvRowError(record.lineNumber(), problem.title(), problem.getMessage()));
            }
        }
        return new CsvParseResult(List.copyOf(rows), List.copyOf(errors));
    }

    private static String readText(InputStream inputStream) {
        if (inputStream == null) {
            throw new IllegalArgumentException("inputStream no puede ser null");
        }
        try {
            String text = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            return text.startsWith("\uFEFF") ? text.substring(1) : text;
        } catch (IOException e) {
            throw new IllegalArgumentException("no se pudo leer el archivo", e);
        }
    }

    private static char detectDelimiter(String text) {
        String firstLine = text.lines().filter(line -> !line.isBlank()).findFirst().orElse("");
        int semicolons = 0;
        int commas = 0;
        boolean inQuotes = false;
        for (int i = 0; i < firstLine.length(); i++) {
            char c = firstLine.charAt(i);
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (!inQuotes) {
                if (c == ';') {
                    semicolons++;
                } else if (c == ',') {
                    commas++;
                }
            }
        }
        return commas > semicolons ? ',' : ';';
    }

    private static List<Record> tokenize(String text, char delimiter) {
        List<Record> records = new ArrayList<>();
        List<String> fields = new ArrayList<>();
        StringBuilder field = new StringBuilder();
        int line = 1;
        int recordLine = 1;
        boolean inQuotes = false;
        boolean quoted = false;

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (inQuotes) {
                if (c == '"') {
                    if (i + 1 < text.length() && text.charAt(i + 1) == '"') {
                        field.append('"');
                        i++;
                    } else {
                        inQuotes = false;
                    }
                } else if (c == '\r' || c == '\n') {
                    if (c == '\r' && i + 1 < text.length() && text.charAt(i + 1) == '\n') {
                        i++;
                    }
                    field.append('\n');
                    line++;
                } else {
                    field.append(c);
                }
            } else if (c == '"' && !quoted && field.isEmpty()) {
                inQuotes = true;
                quoted = true;
            } else if (c == delimiter) {
                fields.add(field.toString());
                field.setLength(0);
                quoted = false;
            } else if (c == '\r' || c == '\n') {
                if (c == '\r' && i + 1 < text.length() && text.charAt(i + 1) == '\n') {
                    i++;
                }
                fields.add(field.toString());
                records.add(new Record(recordLine, List.copyOf(fields), false));
                fields.clear();
                field.setLength(0);
                quoted = false;
                line++;
                recordLine = line;
            } else {
                field.append(c);
            }
        }
        if (inQuotes || !field.isEmpty() || !fields.isEmpty()) {
            fields.add(field.toString());
            records.add(new Record(recordLine, List.copyOf(fields), inQuotes));
        }
        return records;
    }

    private static Header headerOf(Record record) {
        int titleIndex = -1;
        int malIdIndex = -1;
        List<ScoreColumn> scoreColumns = new ArrayList<>();
        for (int i = 0; i < record.fields().size(); i++) {
            String name = normalize(record.fields().get(i));
            if (titleIndex < 0 && TITLE_ALIASES.contains(name)) {
                titleIndex = i;
            } else if (malIdIndex < 0 && MAL_ID_ALIASES.contains(name)) {
                malIdIndex = i;
            } else {
                Matcher matcher = SCORE_HEADER.matcher(name);
                if (matcher.matches()) {
                    int order = matcher.group(2) == null ? i : Integer.parseInt(matcher.group(2));
                    scoreColumns.add(new ScoreColumn(i, order));
                }
            }
        }
        if (titleIndex < 0) {
            throw new IllegalArgumentException("no se encontró columna de nombre");
        }
        scoreColumns.sort(Comparator.comparingInt(ScoreColumn::order));
        return new Header(titleIndex, malIdIndex, scoreColumns.stream().map(ScoreColumn::index).toList());
    }

    private static CsvAnimeRow toHeaderlessRow(Record record) {
        List<Integer> scoreColumns = new ArrayList<>();
        for (int i = 1; i < record.fields().size(); i++) {
            scoreColumns.add(i);
        }
        return toRow(record, new Header(0, -1, scoreColumns));
    }

    private static CsvAnimeRow toRow(Record record, Header header) {
        String title = cell(record, header.titleIndex()).trim();
        if (record.unclosedQuote()) {
            throw new RowProblem(title, "comilla sin cerrar");
        }
        if (title.isEmpty()) {
            throw new RowProblem(title, "sin nombre");
        }
        Integer malId = null;
        if (header.malIdIndex() >= 0) {
            String raw = cell(record, header.malIdIndex()).trim();
            if (!raw.isEmpty()) {
                try {
                    malId = Integer.valueOf(raw);
                } catch (NumberFormatException e) {
                    throw new RowProblem(title, "mal_id inválido");
                }
            }
        }
        List<Integer> scores = new ArrayList<>();
        for (int column : header.scoreColumns()) {
            scores.add(parseScore(cell(record, column), title));
        }
        while (!scores.isEmpty() && scores.getLast() == null) {
            scores.removeLast();
        }
        if (scores.isEmpty()) {
            throw new RowProblem(title, "sin calificaciones");
        }
        return new CsvAnimeRow(record.lineNumber(), title, malId, Collections.unmodifiableList(scores));
    }

    private static Integer parseScore(String cell, String title) {
        String value = cell.trim();
        if (value.isEmpty()) {
            return null;
        }
        BigDecimal number;
        try {
            number = new BigDecimal(value.replace(',', '.'));
        } catch (NumberFormatException e) {
            throw new RowProblem(title, "calificación inválida");
        }
        BigDecimal rounded = number.setScale(0, RoundingMode.HALF_UP);
        if (rounded.compareTo(MIN_SCORE) < 0 || rounded.compareTo(MAX_SCORE) > 0) {
            throw new RowProblem(title, "calificación fuera de rango (1-10)");
        }
        return rounded.intValue();
    }

    private static String cell(Record record, int index) {
        return index < record.fields().size() ? record.fields().get(index) : "";
    }

    private static boolean isBlank(Record record) {
        return record.fields().stream().allMatch(String::isBlank);
    }

    private static String normalize(String raw) {
        return Normalizer.normalize(raw.trim().toLowerCase(Locale.ROOT), Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "");
    }

    private record Record(int lineNumber, List<String> fields, boolean unclosedQuote) {
    }

    private record Header(int titleIndex, int malIdIndex, List<Integer> scoreColumns) {
    }

    private record ScoreColumn(int index, int order) {
    }

    private static final class RowProblem extends RuntimeException {
        private final String title;

        private RowProblem(String title, String message) {
            super(message);
            this.title = Objects.requireNonNull(title);
        }

        private String title() {
            return title;
        }
    }
}
