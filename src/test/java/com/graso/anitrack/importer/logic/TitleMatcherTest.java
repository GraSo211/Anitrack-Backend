package com.graso.anitrack.importer.logic;

import com.graso.anitrack.importer.model.Candidate;
import com.graso.anitrack.importer.model.SearchItem;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class TitleMatcherTest {

    private final TitleMatcher matcher = new TitleMatcher();

    private static SearchItem item(int malId, String title, String en, String ja, List<String> synonyms) {
        java.util.List<String> alternatives = new java.util.ArrayList<>();
        if (en != null) {
            alternatives.add(en);
        }
        if (ja != null) {
            alternatives.add(ja);
        }
        alternatives.addAll(synonyms);
        return new SearchItem(malId, title, List.copyOf(alternatives), "tv", 12, "2024-01-01", null);
    }

    @Test
    void normalizesAccentsCaseAndPunctuation() {
        assertThat(TitleMatcher.normalize("  Shingeki no Kyojin: ¡El Ataque! ")).isEqualTo("shingeki no kyojin el ataque");
        assertThat(TitleMatcher.normalize("Sousou no Frieren")).isEqualTo("sousou no frieren");
        assertThat(TitleMatcher.normalize(null)).isEmpty();
    }

    @Test
    void matchesExactAlternativeEnglishTitle() {
        TitleMatcher.Result result = matcher.match("Attack on Titan", List.of(
                item(16498, "Shingeki no Kyojin", "Attack on Titan", "進撃の巨人", List.of("AoT"))));

        assertThat(result.malId()).isEqualTo(16498);
        assertThat(result.candidates()).extracting(Candidate::malId).containsExactly(16498);
    }

    @Test
    void twoDistinctExactHitsAreAmbiguous() {
        TitleMatcher.Result result = matcher.match("Frieren", List.of(
                item(1, "Frieren", null, null, List.of()),
                item(2, "Sousou no Frieren", "Frieren: Beyond Journey's End", null, List.of("Frieren"))));

        assertThat(result.malId()).isNull();
        assertThat(result.candidates()).extracting(Candidate::malId).containsExactlyInAnyOrder(1, 2);
        assertThat(result.message()).isNotBlank();
    }

    @Test
    void singleSearchResultResolvesAsFallback() {
        TitleMatcher.Result result = matcher.match("Frieren", List.of(
                item(52991, "Sousou no Frieren", null, null, List.of())));

        assertThat(result.malId()).isEqualTo(52991);
        assertThat(result.message()).isEqualTo("Coincidencia única por búsqueda");
    }

    @Test
    void severalSearchResultsWithoutExactMatchAreAmbiguous() {
        TitleMatcher.Result result = matcher.match("Frieren", List.of(
                item(1, "Sousou no Frieren", null, null, List.of()),
                item(2, "Frieren Movie", null, null, List.of())));

        assertThat(result.malId()).isNull();
        assertThat(result.candidates()).hasSize(2);
    }

    @Test
    void zeroResultsIsNotFound() {
        TitleMatcher.Result result = matcher.match("Nada que ver", List.of());

        assertThat(result.malId()).isNull();
        assertThat(result.candidates()).isEmpty();
        assertThat(result.message()).isEqualTo("Sin resultados en MAL");
    }
}
