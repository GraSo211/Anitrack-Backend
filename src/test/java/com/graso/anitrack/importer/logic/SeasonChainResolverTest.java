package com.graso.anitrack.importer.logic;

import com.graso.anitrack.importer.model.ImportAnimeDetail;
import com.graso.anitrack.importer.model.ImportRelation;
import com.graso.anitrack.importer.model.ImportSeason;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.IntFunction;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SeasonChainResolverTest {

    private final SeasonChainResolver resolver = new SeasonChainResolver();

    private static ImportRelation rel(int id, String type) {
        return new ImportRelation(id, type);
    }

    private static ImportAnimeDetail anime(int id, ImportRelation... relations) {
        return new ImportAnimeDetail(id, "Anime " + id, 12, "tv", "2024-01-01", null, List.of(relations));
    }

    private static IntFunction<ImportAnimeDetail> fetcher(Map<Integer, ImportAnimeDetail> details) {
        return id -> {
            ImportAnimeDetail detail = details.get(id);
            if (detail == null) {
                throw new IllegalStateException("fetch inesperado de " + id);
            }
            return detail;
        };
    }

    @Test
    void walksPrequelsToRootAndReturnsFullChainInOrder() {
        Map<Integer, ImportAnimeDetail> details = new HashMap<>();
        details.put(1, anime(1, rel(2, "sequel")));
        details.put(2, anime(2, rel(1, "prequel"), rel(3, "sequel")));
        details.put(3, anime(3, rel(2, "prequel")));

        List<ImportSeason> chain = resolver.resolve(details.get(2), fetcher(details));

        assertThat(chain).extracting(ImportSeason::malId).containsExactly(1, 2, 3);
        assertThat(chain).extracting(ImportSeason::order).containsExactly(1, 2, 3);
        assertThat(chain.get(1).title()).isEqualTo("Anime 2");
    }

    @Test
    void stopsOnCycle() {
        Map<Integer, ImportAnimeDetail> details = new HashMap<>();
        details.put(1, anime(1, rel(2, "sequel")));
        details.put(2, anime(2, rel(1, "sequel")));

        List<ImportSeason> chain = resolver.resolve(details.get(1), fetcher(details));

        assertThat(chain).extracting(ImportSeason::malId).containsExactly(1, 2);
    }

    @Test
    void ignoresSideStoriesSummariesAndMovies() {
        Map<Integer, ImportAnimeDetail> details = new HashMap<>();
        details.put(1, anime(1,
                rel(2, "sequel"),
                rel(99, "side_story"),
                rel(98, "summary"),
                rel(97, "alternative_setting")));
        details.put(2, anime(2, rel(1, "prequel")));

        List<ImportSeason> chain = resolver.resolve(details.get(1), fetcher(details));

        assertThat(chain).extracting(ImportSeason::malId).containsExactly(1, 2);
    }

    @Test
    void multipleSequelsTieBreakBySmallestId() {
        Map<Integer, ImportAnimeDetail> details = new HashMap<>();
        details.put(1, anime(1, rel(5, "sequel"), rel(4, "sequel")));
        details.put(4, anime(4, rel(1, "prequel")));
        details.put(5, anime(5, rel(1, "prequel")));

        List<ImportSeason> chain = resolver.resolve(details.get(1), fetcher(details));

        assertThat(chain).extracting(ImportSeason::malId).containsExactly(1, 4);
    }

    @Test
    void capsSeasonsAtTwenty() {
        Map<Integer, ImportAnimeDetail> details = new HashMap<>();
        for (int id = 1; id <= 30; id++) {
            List<ImportRelation> relations = new ArrayList<>();
            if (id > 1) {
                relations.add(rel(id - 1, "prequel"));
            }
            if (id < 30) {
                relations.add(rel(id + 1, "sequel"));
            }
            details.put(id, new ImportAnimeDetail(id, "Anime " + id, 12, "tv", null, null, List.copyOf(relations)));
        }

        List<ImportSeason> chain = resolver.resolve(details.get(1), fetcher(details));

        assertThat(chain).hasSize(20);
        assertThat(chain.get(0).malId()).isEqualTo(1);
        assertThat(chain.get(19).malId()).isEqualTo(20);
    }

    @Test
    void capsPrequelWalkAtThirtyHops() {
        Map<Integer, ImportAnimeDetail> details = new HashMap<>();
        for (int id = 1; id <= 40; id++) {
            List<ImportRelation> relations = new ArrayList<>();
            if (id > 1) {
                relations.add(rel(id - 1, "prequel"));
            }
            if (id < 40) {
                relations.add(rel(id + 1, "sequel"));
            }
            details.put(id, new ImportAnimeDetail(id, "Anime " + id, 12, "tv", null, null, List.copyOf(relations)));
        }

        List<ImportSeason> chain = resolver.resolve(details.get(40), fetcher(details));

        assertThat(chain).hasSize(20);
        assertThat(chain.get(0).malId()).isEqualTo(10);
    }

    @Test
    void propagatesFetchFailures() {
        Map<Integer, ImportAnimeDetail> details = new HashMap<>();
        details.put(1, anime(1, rel(2, "sequel")));

        assertThatThrownBy(() -> resolver.resolve(details.get(1), fetcher(details)))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("fetch inesperado de 2");
    }
}
