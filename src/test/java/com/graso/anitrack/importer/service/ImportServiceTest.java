package com.graso.anitrack.importer.service;

import com.graso.anitrack.animelist.client.myanimelist.MyAnimeListAnimeListClient;
import com.graso.anitrack.animelist.model.AnimeItem;
import com.graso.anitrack.animelist.model.AnimeList;
import com.graso.anitrack.animelist.model.Status;
import com.graso.anitrack.importer.client.MyAnimeListImportMapper;
import com.graso.anitrack.importer.client.dto.ResponseAnimeDetailsRequest;
import com.graso.anitrack.importer.client.dto.ResponseAnimeSearchRequest;
import com.graso.anitrack.importer.model.ImportJob;
import com.graso.anitrack.importer.model.ImportPhase;
import com.graso.anitrack.importer.model.RowStatus;
import com.graso.anitrack.importer.model.SeasonAction;
import com.graso.anitrack.importer.store.ImportJobStore;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ImportServiceTest {

    private final MyAnimeListAnimeListClient client = mock(MyAnimeListAnimeListClient.class);
    private final ImportService service = new ImportService(
            client,
            new MyAnimeListImportMapper(),
            new ImportJobStore(),
            new MalRateLimiter(0, 0),
            Runnable::run);

    private static InputStream csv(String content) {
        return new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
    }

    private static ResponseAnimeSearchRequest searchResponse() {
        return new ResponseAnimeSearchRequest(
                List.of(new ResponseAnimeSearchRequest.Data(new ResponseAnimeSearchRequest.Node(
                        52991,
                        "Sousou no Frieren",
                        null,
                        new ResponseAnimeSearchRequest.Node.AlternativeTitles(
                                "Frieren: Beyond Journey's End", "葬送のフリーレン", List.of("Frieren")),
                        28,
                        "tv",
                        "2023-09-29"))),
                null);
    }

    private static ResponseAnimeDetailsRequest details() {
        return new ResponseAnimeDetailsRequest(
                52991,
                "Sousou no Frieren",
                null,
                28,
                "tv",
                "2023-09-29",
                null,
                List.of());
    }

    @Test
    void happyPathResolvesAndPatchesCompletedWithScore() {
        when(client.getAnimeList("tok", null)).thenReturn(new AnimeList(List.of()));
        when(client.searchAnime("tok", "Sousou no Frieren")).thenReturn(searchResponse());
        when(client.getAnimeDetails("tok", 52991)).thenReturn(details());

        String jobId = service.createJob(csv("nombre;calificacion\nSousou no Frieren;9\n"), "tok");
        ImportJob job = service.get(jobId);

        assertThat(job.getPhase()).isEqualTo(ImportPhase.PREVIEW_READY);
        assertThat(job.previewRows()).singleElement().satisfies(row -> {
            assertThat(row.status()).isEqualTo(RowStatus.READY);
            assertThat(row.seasons()).singleElement().satisfies(season -> {
                assertThat(season.malId()).isEqualTo(52991);
                assertThat(season.score()).isEqualTo(9);
                assertThat(season.episodes()).isEqualTo(28);
            });
        });

        service.start(jobId, "tok", List.of(), List.of());

        assertThat(job.getPhase()).isEqualTo(ImportPhase.DONE);
        assertThat(job.previewRows()).singleElement()
                .satisfies(row -> assertThat(row.status()).isEqualTo(RowStatus.SYNCED));
        assertThat(job.getCounts().synced()).isEqualTo(1);
        verify(client).patchListStatus("tok", 52991, "completed", 9, 28);
    }

    @Test
    void rowWithoutScoreCompletesTheBaseEntryWithoutSendingScore() {
        when(client.getAnimeList("tok", null)).thenReturn(new AnimeList(List.of()));
        when(client.searchAnime("tok", "Sousou no Frieren")).thenReturn(searchResponse());
        when(client.getAnimeDetails("tok", 52991)).thenReturn(details());

        String jobId = service.createJob(csv("nombre;calificacion\nSousou no Frieren;\n"), "tok");
        ImportJob job = service.get(jobId);

        assertThat(job.previewRows()).singleElement().satisfies(row -> {
            assertThat(row.status()).isEqualTo(RowStatus.READY);
            assertThat(row.seasons()).singleElement().satisfies(season -> {
                assertThat(season.score()).isZero();
                assertThat(season.episodes()).isEqualTo(28);
            });
        });

        service.start(jobId, "tok", List.of(), List.of());

        verify(client).patchListStatus("tok", 52991, "completed", null, 28);
        assertThat(job.getCounts().synced()).isEqualTo(1);
    }

    @Test
    void expiredTokenFailsTheJobWithTheExactMessage() {
        when(client.getAnimeList("expired", null)).thenThrow(
                WebClientResponseException.create(401, "Unauthorized", HttpHeaders.EMPTY, new byte[0], null));

        String jobId = service.createJob(csv("nombre;calificacion\nSousou no Frieren;9\n"), "expired");
        ImportJob job = service.get(jobId);

        assertThat(job.getPhase()).isEqualTo(ImportPhase.FAILED);
        assertThat(job.getError()).isEqualTo(
                "Tu sesión de MAL expiró durante la importación. Volvé a iniciar sesión y usá «Reintentar fallidos».");
    }

    @Test
    void existingEntryIsKeptUnlessTheUserConfirmsOverwrite() {
        when(client.getAnimeList("tok", null)).thenReturn(new AnimeList(List.of(
                new AnimeItem(52991, "Sousou no Frieren", null, Status.COMPLETED, 7, 28, false))));
        when(client.searchAnime("tok", "Sousou no Frieren")).thenReturn(searchResponse());
        when(client.getAnimeDetails("tok", 52991)).thenReturn(details());

        String jobId = service.createJob(csv("nombre;calificacion\nSousou no Frieren;9\n"), "tok");
        ImportJob job = service.get(jobId);
        assertThat(job.previewRows()).singleElement().satisfies(row ->
                assertThat(row.seasons()).singleElement().satisfies(season -> {
                    assertThat(season.action()).isEqualTo(SeasonAction.KEEP);
                    assertThat(season.existing().score()).isEqualTo(7);
                }));

        service.start(jobId, "tok", List.of(), List.of());
        verify(client, never()).patchListStatus("tok", 52991, "completed", 9, 28);
        assertThat(job.getCounts().skipped()).isEqualTo(1);

        String confirmedJobId = service.createJob(csv("nombre;calificacion\nSousou no Frieren;9\n"), "tok");
        service.start(confirmedJobId, "tok", List.of(52991), List.of());
        verify(client).patchListStatus("tok", 52991, "completed", 9, 28);
    }

    @Test
    void excludedSeasonsAreNotSynced() {
        when(client.getAnimeList("tok", null)).thenReturn(new AnimeList(List.of()));
        when(client.searchAnime("tok", "Sousou no Frieren")).thenReturn(searchResponse());
        when(client.getAnimeDetails("tok", 52991)).thenReturn(details());

        String jobId = service.createJob(csv("nombre;calificacion\nSousou no Frieren;9\n"), "tok");
        ImportJob job = service.get(jobId);
        service.start(jobId, "tok", List.of(), List.of(52991));

        verify(client, never()).patchListStatus("tok", 52991, "completed", 9, 28);
        assertThat(job.previewRows()).singleElement().satisfies(row ->
                assertThat(row.seasons()).singleElement()
                        .satisfies(season -> assertThat(season.action()).isEqualTo(SeasonAction.SKIP)));
    }

    @Test
    void unknownJobIdIsABadRequest() {
        org.assertj.core.api.Assertions.assertThatThrownBy(() -> service.get("no-existe"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Importación no encontrada o expirada");
    }
}
