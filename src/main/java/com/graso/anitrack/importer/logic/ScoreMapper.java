package com.graso.anitrack.importer.logic;

import com.graso.anitrack.importer.model.ExistingStatus;
import com.graso.anitrack.importer.model.ImportSeason;
import com.graso.anitrack.importer.model.SeasonAction;
import com.graso.anitrack.importer.model.SeasonPreview;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ScoreMapper {

    public record Result(List<SeasonPreview> seasons, String warning) {
    }

    public Result map(List<Integer> scores, List<ImportSeason> chain, Map<Integer, ExistingStatus> existing) {
        List<SeasonPreview> seasons = new ArrayList<>();
        int mapped = Math.min(scores.size(), chain.size());
        for (int position = 0; position < mapped; position++) {
            Integer score = scores.get(position);
            if (score == null) {
                continue;
            }
            ImportSeason season = chain.get(position);
            ExistingStatus current = existing.get(season.malId());
            seasons.add(new SeasonPreview(
                    season.malId(),
                    season.title(),
                    season.order(),
                    season.episodes(),
                    season.image(),
                    score,
                    true,
                    current,
                    current == null ? SeasonAction.CREATE : SeasonAction.KEEP,
                    null));
        }

        String warning = null;
        if (scores.size() > chain.size()) {
            warning = "Solo se mapearon " + chain.size() + " de " + scores.size()
                    + " calificaciones: la franquicia tiene " + chain.size() + " temporadas";
        }
        return new Result(List.copyOf(seasons), warning);
    }
}
