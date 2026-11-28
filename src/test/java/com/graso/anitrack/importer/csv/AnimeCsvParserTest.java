package com.graso.anitrack.importer.csv;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

class AnimeCsvParserTest {

    private final AnimeCsvParser parser = new AnimeCsvParser();

    private static InputStream csv(String content) {
        return new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
    }

    private CsvParseResult parse(String content) {
        return parser.parse(csv(content));
    }

    @Test
    void parsesSemicolonFileWithoutHeader() {
        CsvParseResult result = parse("Frieren;9");

        assertThat(result.errors()).isEmpty();
        assertThat(result.rows()).singleElement().satisfies(row -> {
            assertThat(row.lineNumber()).isEqualTo(1);
            assertThat(row.title()).isEqualTo("Frieren");
            assertThat(row.malId()).isNull();
            assertThat(row.scores()).containsExactly(9);
        });
    }

    @Test
    void ordersScoreColumnsBySuffix() {
        CsvParseResult result = parse("nombre;calificacion_2;calificacion_1\nFrieren;8;9");

        assertThat(result.errors()).isEmpty();
        assertThat(result.rows()).singleElement().satisfies(row -> {
            assertThat(row.lineNumber()).isEqualTo(2);
            assertThat(row.scores()).containsExactly(9, 8);
        });
    }

    @Test
    void readsMalIdColumn() {
        CsvParseResult result = parse("nombre;mal_id;calificacion\nFrieren;52991;9");

        assertThat(result.errors()).isEmpty();
        assertThat(result.rows()).singleElement().satisfies(row -> {
            assertThat(row.malId()).isEqualTo(52991);
            assertThat(row.scores()).containsExactly(9);
        });
    }

    @Test
    void nonNumericMalIdIsRowError() {
        CsvParseResult result = parse("nombre;mal_id;calificacion\nFrieren;abc;9");

        assertThat(result.rows()).isEmpty();
        assertThat(result.errors()).singleElement().satisfies(error -> {
            assertThat(error.lineNumber()).isEqualTo(2);
            assertThat(error.title()).isEqualTo("Frieren");
            assertThat(error.message()).isEqualTo("mal_id inválido");
        });
    }

    @Test
    void quotedTitleMayContainDelimiter() {
        CsvParseResult result = parse("\"Kaguya-sama, love is war\";8");

        assertThat(result.errors()).isEmpty();
        assertThat(result.rows()).singleElement().satisfies(row -> {
            assertThat(row.title()).isEqualTo("Kaguya-sama, love is war");
            assertThat(row.scores()).containsExactly(8);
        });
    }

    @Test
    void quotedTitleMayContainDoubledQuotes() {
        CsvParseResult result = parse("\"Kaguya \"\"love is war\"\"\";8");

        assertThat(result.errors()).isEmpty();
        assertThat(result.rows()).singleElement()
                .extracting(CsvAnimeRow::title)
                .isEqualTo("Kaguya \"love is war\"");
    }

    @Test
    void parsesCommaDelimitedFileAndDecimalCommaScore() {
        CsvParseResult commaFile = parse("nombre,calificacion\nFrieren,\"8,5\"");
        assertThat(commaFile.errors()).isEmpty();
        assertThat(commaFile.rows()).singleElement().satisfies(row -> {
            assertThat(row.title()).isEqualTo("Frieren");
            assertThat(row.scores()).containsExactly(9);
        });

        CsvParseResult decimalComma = parse("Frieren;8,5");
        assertThat(decimalComma.errors()).isEmpty();
        assertThat(decimalComma.rows()).singleElement()
                .extracting(CsvAnimeRow::scores)
                .isEqualTo(List.of(9));
    }

    @Test
    void stripsBomAndHandlesCrlf() {
        CsvParseResult result = parse("\uFEFFnombre;calificacion\r\nFrieren;9\r\n");

        assertThat(result.errors()).isEmpty();
        assertThat(result.rows()).singleElement().satisfies(row -> {
            assertThat(row.lineNumber()).isEqualTo(2);
            assertThat(row.title()).isEqualTo("Frieren");
            assertThat(row.scores()).containsExactly(9);
        });
    }

    @Test
    void emptyScoreCellBecomesNullAndTrailingNullsAreTrimmed() {
        CsvParseResult result = parse("Frieren;8;;;");

        assertThat(result.errors()).isEmpty();
        assertThat(result.rows()).singleElement()
                .extracting(CsvAnimeRow::scores)
                .isEqualTo(java.util.List.of(8));

        CsvParseResult withGap = parse("Frieren;;8");
        assertThat(withGap.rows()).singleElement()
                .extracting(CsvAnimeRow::scores)
                .isEqualTo(Arrays.asList(null, 8));
    }

    @Test
    void rowWithoutScoresIsValidAndMeansViewedWithoutRating() {
        CsvParseResult result = parse("Frieren;;");

        assertThat(result.errors()).isEmpty();
        assertThat(result.rows()).singleElement()
                .extracting(CsvAnimeRow::scores)
                .isEqualTo(List.of());
    }

    @Test
    void dashAndXMeanViewedWithoutScore() {
        CsvParseResult result = parse("Frieren;-\n\"Steins;Gate\";X");

        assertThat(result.errors()).isEmpty();
        assertThat(result.rows()).extracting(CsvAnimeRow::scores)
                .containsExactly(List.of(0), List.of(0));
    }

    @Test
    void scoreZeroAndElevenAreOutOfRange() {
        CsvParseResult result = parse("Frieren;0\n\"Steins;Gate\";11");

        assertThat(result.rows()).isEmpty();
        assertThat(result.errors()).hasSize(2)
                .allSatisfy(error -> assertThat(error.message())
                        .isEqualTo("calificación fuera de rango (1-10). Usá - o x para \"vista sin nota\""));
    }

    @Test
    void moreThanTwoThousandRowsIsRejected() {
        StringBuilder content = new StringBuilder();
        for (int i = 0; i < 2001; i++) {
            content.append("Anime ").append(i).append(";9\n");
        }

        assertThatIllegalArgumentException()
                .isThrownBy(() -> parse(content.toString()))
                .withMessageContaining("2000");
    }

    @Test
    void blankLinesAndEmptyRecordsAreSkipped() {
        CsvParseResult result = parse("Frieren;9\n\n;;\n\n\"Steins;Gate\";10\n");

        assertThat(result.errors()).isEmpty();
        assertThat(result.rows()).hasSize(2);
        assertThat(result.rows()).extracting(CsvAnimeRow::lineNumber)
                .containsExactly(1, 5);
        assertThat(result.rows()).extracting(CsvAnimeRow::title)
                .containsExactly("Frieren", "Steins;Gate");
    }

    @Test
    void unclosedQuoteIsRowError() {
        CsvParseResult result = parse("\"Frieren;9");

        assertThat(result.rows()).isEmpty();
        assertThat(result.errors()).singleElement()
                .extracting(CsvRowError::message)
                .isEqualTo("comilla sin cerrar");
    }
}
