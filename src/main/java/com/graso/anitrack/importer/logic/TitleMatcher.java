package com.graso.anitrack.importer.logic;

import com.graso.anitrack.importer.model.Candidate;
import com.graso.anitrack.importer.model.SearchItem;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class TitleMatcher {

    private static final int MAX_CANDIDATES = 5;

    public record Result(Integer malId, List<Candidate> candidates, String message) {
    }

    public Result match(String csvTitle, List<SearchItem> results) {
        String wanted = normalize(csvTitle);
        Map<Integer, Candidate> exact = new LinkedHashMap<>();
        if (!wanted.isEmpty() && results != null) {
            for (SearchItem item : results) {
                if (anyTitleNormalizesTo(item, wanted)) {
                    exact.putIfAbsent(item.malId(), toCandidate(item));
                }
            }
        }

        if (exact.size() == 1) {
            return new Result(exact.keySet().iterator().next(), List.copyOf(exact.values()), null);
        }
        if (exact.size() > 1) {
            return new Result(null, List.copyOf(exact.values()), "Varias coincidencias exactas: elegí la entrada correcta");
        }
        if (results == null || results.isEmpty()) {
            return new Result(null, List.of(), "Sin resultados en MAL");
        }
        if (results.size() == 1) {
            SearchItem only = results.get(0);
            return new Result(only.malId(), List.of(toCandidate(only)), "Coincidencia única por búsqueda");
        }
        List<Candidate> candidates = new ArrayList<>();
        for (SearchItem item : results.subList(0, Math.min(results.size(), MAX_CANDIDATES))) {
            candidates.add(toCandidate(item));
        }
        return new Result(null, List.copyOf(candidates), "Varias coincidencias por búsqueda: elegí la entrada correcta");
    }

    public static String normalize(String raw) {
        if (raw == null) {
            return "";
        }
        return Normalizer.normalize(raw, Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "")
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^\\p{L}\\p{N}]+", " ")
                .trim();
    }

    private static boolean anyTitleNormalizesTo(SearchItem item, String wanted) {
        if (normalize(item.title()).equals(wanted)) {
            return true;
        }
        for (String alternative : item.alternativeTitles()) {
            if (normalize(alternative).equals(wanted)) {
                return true;
            }
        }
        return false;
    }

    private static Candidate toCandidate(SearchItem item) {
        return new Candidate(item.malId(), item.title(), item.mediaType(), item.episodes(), item.startDate(), item.image());
    }
}
