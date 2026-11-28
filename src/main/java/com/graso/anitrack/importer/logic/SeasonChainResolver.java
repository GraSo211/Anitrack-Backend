package com.graso.anitrack.importer.logic;

import com.graso.anitrack.importer.model.ImportAnimeDetail;
import com.graso.anitrack.importer.model.ImportRelation;
import com.graso.anitrack.importer.model.ImportSeason;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.IntFunction;

public class SeasonChainResolver {

    private static final int MAX_PREQUELS = 30;
    private static final int MAX_SEASONS = 20;

    public List<ImportSeason> resolve(ImportAnimeDetail base, IntFunction<ImportAnimeDetail> detailFetcher) {
        Set<Integer> walkedBack = new HashSet<>();
        walkedBack.add(base.id());
        ImportAnimeDetail root = base;
        for (int hop = 0; hop < MAX_PREQUELS; hop++) {
            ImportAnimeDetail previous = follow(root, "prequel", detailFetcher, walkedBack);
            if (previous == null) {
                break;
            }
            walkedBack.add(previous.id());
            root = previous;
        }

        Set<Integer> walkedForward = new HashSet<>();
        walkedForward.add(root.id());
        List<ImportSeason> seasons = new ArrayList<>();
        ImportAnimeDetail current = root;
        for (int order = 1; order <= MAX_SEASONS; order++) {
            seasons.add(new ImportSeason(
                    current.id(),
                    current.title(),
                    current.numEpisodes(),
                    current.startDate(),
                    current.image(),
                    order));
            ImportAnimeDetail next = follow(current, "sequel", detailFetcher, walkedForward);
            if (next == null) {
                break;
            }
            walkedForward.add(next.id());
            current = next;
        }
        return seasons;
    }

    // shortcut: con varias secuelas/precuelas nos quedamos con el id más chico; alcanza para franquicias MAL,
    // si aparecen splits reales hay que mirar start_date.
    private ImportAnimeDetail follow(
            ImportAnimeDetail detail,
            String relationType,
            IntFunction<ImportAnimeDetail> detailFetcher,
            Set<Integer> visited) {
        if (detail.relations() == null) {
            return null;
        }
        Integer nextId = null;
        for (ImportRelation relation : detail.relations()) {
            if (relation == null || !relationType.equals(relation.relationType()) || visited.contains(relation.id())) {
                continue;
            }
            if (nextId == null || relation.id() < nextId) {
                nextId = relation.id();
            }
        }
        return nextId == null ? null : detailFetcher.apply(nextId);
    }
}
