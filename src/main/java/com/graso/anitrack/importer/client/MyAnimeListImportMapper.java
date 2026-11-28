package com.graso.anitrack.importer.client;

import com.graso.anitrack.importer.client.dto.ResponseAnimeDetailsRequest;
import com.graso.anitrack.importer.client.dto.ResponseAnimeSearchRequest;
import com.graso.anitrack.importer.model.Candidate;
import com.graso.anitrack.importer.model.ImportAnimeDetail;
import com.graso.anitrack.importer.model.ImportRelation;
import com.graso.anitrack.importer.model.SearchItem;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class MyAnimeListImportMapper {

    private static final int SEARCH_LIMIT = 10;

    public ImportAnimeDetail toDetail(ResponseAnimeDetailsRequest response) {
        List<ImportRelation> relations = new ArrayList<>();
        if (response.relatedAnime() != null) {
            for (ResponseAnimeDetailsRequest.Related related : response.relatedAnime()) {
                if (related != null && related.node() != null) {
                    relations.add(new ImportRelation(related.node().id(), related.relationType()));
                }
            }
        }
        return new ImportAnimeDetail(
                response.id(),
                response.title(),
                response.numEpisodes(),
                response.mediaType(),
                response.startDate(),
                image(response.mainPicture()),
                relations);
    }

    public List<SearchItem> toSearchItems(ResponseAnimeSearchRequest response) {
        if (response == null || response.data() == null) {
            return List.of();
        }
        return response.data().stream()
                .limit(SEARCH_LIMIT)
                .map(ResponseAnimeSearchRequest.Data::node)
                .filter(node -> node != null)
                .map(this::toSearchItem)
                .toList();
    }

    public Candidate toCandidate(SearchItem item) {
        return new Candidate(item.malId(), item.title(), item.mediaType(), item.episodes(), item.startDate(), item.image());
    }

    public Candidate toCandidate(ImportAnimeDetail detail) {
        return new Candidate(detail.id(), detail.title(), detail.mediaType(), detail.numEpisodes(), detail.startDate(), detail.image());
    }

    private SearchItem toSearchItem(ResponseAnimeSearchRequest.Node node) {
        List<String> titles = new ArrayList<>();
        ResponseAnimeSearchRequest.Node.AlternativeTitles alternative = node.alternativeTitles();
        if (alternative != null) {
            if (alternative.en() != null) {
                titles.add(alternative.en());
            }
            if (alternative.ja() != null) {
                titles.add(alternative.ja());
            }
            if (alternative.synonyms() != null) {
                titles.addAll(alternative.synonyms());
            }
        }
        return new SearchItem(
                node.id(),
                node.title(),
                List.copyOf(titles),
                node.mediaType(),
                node.numEpisodes(),
                node.startDate(),
                image(node.mainPicture()));
    }

    private String image(ResponseAnimeDetailsRequest.Picture picture) {
        if (picture == null) {
            return null;
        }
        return picture.large() != null ? picture.large() : picture.medium();
    }

    private String image(ResponseAnimeSearchRequest.Node.Picture picture) {
        if (picture == null) {
            return null;
        }
        return picture.large() != null ? picture.large() : picture.medium();
    }
}
