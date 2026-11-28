package com.graso.anitrack.importer.logic;

import com.graso.anitrack.importer.model.ExistingStatus;
import com.graso.anitrack.importer.model.ImportSeason;
import com.graso.anitrack.importer.model.SeasonAction;
import com.graso.anitrack.importer.model.SeasonPreview;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ScoreMapperTest {

    private final ScoreMapper mapper = new ScoreMapper();

    private static ImportSeason season(int malId, int order) {
        return new ImportSeason(malId, "Anime " + malId, 12, null, null, order);
    }

    @Test
    void mapsPositionallyAndNullStillConsumesPosition() {
        ScoreMapper.Result result = mapper.map(
                Arrays.asList(9, null, 7),
                List.of(season(1, 1), season(2, 2), season(3, 3)),
                Map.of(),
                1);

        assertThat(result.seasons()).extracting(SeasonPreview::malId).containsExactly(1, 3);
        assertThat(result.seasons()).extracting(SeasonPreview::score).containsExactly(9, 7);
        assertThat(result.warning()).isNull();
    }

    @Test
    void scoresShorterThanChainOnlyMapTheFirstSeasons() {
        ScoreMapper.Result result = mapper.map(
                List.of(8),
                List.of(season(1, 1), season(2, 2), season(3, 3)),
                Map.of(),
                1);

        assertThat(result.seasons()).extracting(SeasonPreview::malId).containsExactly(1);
        assertThat(result.warning()).isNull();
    }

    @Test
    void scoresLongerThanChainWarnAndTruncate() {
        ScoreMapper.Result result = mapper.map(
                List.of(8, 9, 10),
                List.of(season(1, 1), season(2, 2)),
                Map.of(),
                1);

        assertThat(result.seasons()).hasSize(2);
        assertThat(result.warning()).isEqualTo("Solo se mapearon 2 de 3 calificaciones: la franquicia tiene 2 temporadas");
    }

    @Test
    void existingEntryIsKeptAndPopulated() {
        ExistingStatus existing = new ExistingStatus("watching", 7, 5);

        ScoreMapper.Result result = mapper.map(
                List.of(9),
                List.of(season(1, 1)),
                Map.of(1, existing),
                1);

        SeasonPreview preview = result.seasons().get(0);
        assertThat(preview.action()).isEqualTo(SeasonAction.KEEP);
        assertThat(preview.existing()).isEqualTo(existing);
        assertThat(preview.selected()).isTrue();
        assertThat(preview.episodes()).isEqualTo(12);
        assertThat(preview.order()).isEqualTo(1);
    }

    @Test
    void noScoresMarksOnlyTheResolvedBaseEntryWithoutScore() {
        ScoreMapper.Result result = mapper.map(
                List.of(),
                List.of(season(1, 1), season(2, 2), season(3, 3)),
                Map.of(),
                2);

        assertThat(result.seasons()).extracting(SeasonPreview::malId).containsExactly(2);
        assertThat(result.seasons().get(0).score()).isZero();
        assertThat(result.seasons().get(0).action()).isEqualTo(SeasonAction.CREATE);
    }

    @Test
    void zeroMeansViewedWithoutScore() {
        ScoreMapper.Result result = mapper.map(
                Arrays.asList(0, 9),
                List.of(season(1, 1), season(2, 2)),
                Map.of(),
                1);

        assertThat(result.seasons()).extracting(SeasonPreview::score).containsExactly(0, 9);
    }

    @Test
    void seasonWithoutExistingEntryIsCreated() {
        ScoreMapper.Result result = mapper.map(List.of(9), List.of(season(1, 1)), Map.of(), 1);

        assertThat(result.seasons().get(0).action()).isEqualTo(SeasonAction.CREATE);
        assertThat(result.seasons().get(0).existing()).isNull();
    }
}
