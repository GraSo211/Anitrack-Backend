package com.graso.anitrack.importer.service;

import com.graso.anitrack.animelist.client.myanimelist.MyAnimeListAnimeListClient;
import com.graso.anitrack.animelist.model.AnimeItem;
import com.graso.anitrack.animelist.model.AnimeList;
import com.graso.anitrack.importer.client.MyAnimeListImportMapper;
import com.graso.anitrack.importer.client.dto.ResponseAnimeDetailsRequest;
import com.graso.anitrack.importer.client.dto.ResponseAnimeSearchRequest;
import com.graso.anitrack.importer.csv.AnimeCsvParser;
import com.graso.anitrack.importer.csv.CsvAnimeRow;
import com.graso.anitrack.importer.csv.CsvParseResult;
import com.graso.anitrack.importer.csv.CsvRowError;
import com.graso.anitrack.importer.logic.ScoreMapper;
import com.graso.anitrack.importer.logic.SeasonChainResolver;
import com.graso.anitrack.importer.logic.TitleMatcher;
import com.graso.anitrack.importer.model.Candidate;
import com.graso.anitrack.importer.model.ExistingStatus;
import com.graso.anitrack.importer.model.ImportAnimeDetail;
import com.graso.anitrack.importer.model.ImportCounts;
import com.graso.anitrack.importer.model.ImportJob;
import com.graso.anitrack.importer.model.ImportPhase;
import com.graso.anitrack.importer.model.ImportProgress;
import com.graso.anitrack.importer.model.ImportSeason;
import com.graso.anitrack.importer.model.RowDecision;
import com.graso.anitrack.importer.model.RowStatus;
import com.graso.anitrack.importer.model.SeasonAction;
import com.graso.anitrack.importer.model.SeasonPreview;
import com.graso.anitrack.importer.model.SearchItem;
import com.graso.anitrack.importer.store.ImportJobStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;

@Service
public class ImportService {

    static final String TOKEN_EXPIRED =
            "Tu sesión de MAL expiró durante la importación. Volvé a iniciar sesión y usá «Reintentar fallidos».";

    private static final Logger log = LoggerFactory.getLogger(ImportService.class);
    private static final String MAL_STATUS_COMPLETED = "completed";

    private final MyAnimeListAnimeListClient client;
    private final MyAnimeListImportMapper mapper;
    private final ImportJobStore store;
    private final MalRateLimiter rateLimiter;
    private final TaskExecutor importExecutor;
    private final TitleMatcher titleMatcher = new TitleMatcher();
    private final SeasonChainResolver chainResolver = new SeasonChainResolver();
    private final ScoreMapper scoreMapper = new ScoreMapper();
    private final AnimeCsvParser csvParser = new AnimeCsvParser();

    public ImportService(
            MyAnimeListAnimeListClient client,
            MyAnimeListImportMapper mapper,
            ImportJobStore store,
            MalRateLimiter rateLimiter,
            @Qualifier("importExecutor") TaskExecutor importExecutor) {
        this.client = client;
        this.mapper = mapper;
        this.store = store;
        this.rateLimiter = rateLimiter;
        this.importExecutor = importExecutor;
    }

    public String createJob(InputStream csv, String token) {
        CsvParseResult parsed = csvParser.parse(csv);
        ImportJob job = new ImportJob(UUID.randomUUID().toString(), token);
        for (CsvAnimeRow row : parsed.rows()) {
            job.addRow(ImportJob.RowState.pending(row));
        }
        for (CsvRowError error : parsed.errors()) {
            job.addRow(ImportJob.RowState.invalid(error));
        }
        job.setProgress(new ImportProgress(0, job.rowCount(), null));
        job.setCounts(ImportCounts.of(job));
        store.put(job);
        importExecutor.execute(() -> resolveJob(job));
        return job.getJobId();
    }

    public ImportJob get(String jobId) {
        return require(jobId);
    }

    public List<Candidate> search(String token, String query) {
        if (query == null || query.isBlank()) {
            return List.of();
        }
        rateLimiter.acquire();
        ResponseAnimeSearchRequest response = client.searchAnime(token, query);
        return mapper.toSearchItems(response).stream().map(mapper::toCandidate).toList();
    }

    public void start(String jobId, String token, List<Integer> overwriteMalIds, List<Integer> excludedMalIds) {
        ImportJob job = require(jobId);
        job.setToken(token);
        if (job.getPhase() == ImportPhase.SYNCING) {
            throw new IllegalStateException("La importación ya está en curso");
        }
        job.setOverwriteMalIds(overwriteMalIds);
        job.setExcludedMalIds(excludedMalIds);
        job.setPhase(ImportPhase.SYNCING);
        importExecutor.execute(() -> sync(job, rowsWithStatus(job, RowStatus.READY)));
    }

    public void resolve(String jobId, String token, List<RowDecision> decisions) {
        ImportJob job = require(jobId);
        job.setToken(token);
        if (job.getPhase() == ImportPhase.SYNCING) {
            throw new IllegalStateException("La importación ya está en curso");
        }
        ImportPhase phase = job.getPhase();
        boolean syncAfter = phase == ImportPhase.PREVIEW_READY || phase == ImportPhase.DONE;
        if (syncAfter) {
            job.setPhase(ImportPhase.SYNCING);
        }
        importExecutor.execute(() -> reResolve(job, decisions, syncAfter));
    }

    public void retry(String jobId, String token) {
        ImportJob job = require(jobId);
        job.setToken(token);
        if (job.getPhase() == ImportPhase.SYNCING) {
            throw new IllegalStateException("La importación ya está en curso");
        }
        job.setPhase(ImportPhase.SYNCING);
        importExecutor.execute(() -> retryFailed(job));
    }

    public void cancel(String jobId) {
        ImportJob job = require(jobId);
        job.requestCancel();
        if (job.getPhase() == ImportPhase.PREVIEW_READY) {
            job.setCounts(ImportCounts.of(job));
            job.setPhase(ImportPhase.CANCELLED);
        }
    }

    private void resolveJob(ImportJob job) {
        try {
            job.setPhase(ImportPhase.RESOLVING);
            Map<Integer, ExistingStatus> existing = loadExisting(job);
            job.setExisting(existing);
            List<ImportJob.RowState> rows = job.rows();
            job.setProgress(new ImportProgress(0, rows.size(), null));
            int done = 0;
            for (ImportJob.RowState row : rows) {
                if (job.isCancelRequested()) {
                    finishCancelled(job);
                    return;
                }
                if (row.status == RowStatus.PENDING) {
                    resolveRow(job, row, existing);
                }
                job.setProgress(new ImportProgress(++done, rows.size(), row.source.title()));
            }
            job.setCounts(ImportCounts.of(job));
            job.setPhase(ImportPhase.PREVIEW_READY);
        } catch (TokenExpiredException e) {
            failExpired(job);
        } catch (MalThrottledException e) {
            fail(job, "MAL limitó las solicitudes", e);
        } catch (RuntimeException e) {
            fail(job, "Error inesperado durante la resolución", e);
        }
    }

    private Map<Integer, ExistingStatus> loadExisting(ImportJob job) {
        AnimeList list = call(job, "lista del usuario", () -> client.getAnimeList(job.getToken(), null));
        Map<Integer, ExistingStatus> existing = new HashMap<>();
        if (list == null || list.animeList() == null) {
            return existing;
        }
        for (AnimeItem item : list.animeList()) {
            existing.put(item.id(), new ExistingStatus(
                    item.status() == null ? null : item.status().wireValue(),
                    item.score() == null ? 0 : item.score(),
                    item.episodesWatched() == null ? 0 : item.episodesWatched()));
        }
        return existing;
    }

    private void resolveRow(ImportJob job, ImportJob.RowState row, Map<Integer, ExistingStatus> existing) {
        CsvAnimeRow source = row.source;
        try {
            ImportAnimeDetail base;
            List<Candidate> candidates;
            String note = null;
            if (source.malId() != null) {
                base = detail(job, source.malId());
                candidates = List.of(mapper.toCandidate(base));
            } else {
                List<SearchItem> results = search(job, source.title());
                TitleMatcher.Result match = titleMatcher.match(source.title(), results);
                candidates = match.candidates();
                if (match.malId() == null) {
                    row.status = candidates.isEmpty() ? RowStatus.NOT_FOUND : RowStatus.AMBIGUOUS;
                    row.message = match.message();
                    row.candidates = candidates;
                    return;
                }
                note = match.message();
                base = detail(job, match.malId());
            }

            List<ImportSeason> chain = chainResolver.resolve(base, id -> detail(job, id));
            ScoreMapper.Result mapped = scoreMapper.map(source.scores(), chain, existing);
            row.status = RowStatus.READY;
            row.candidates = candidates;
            row.message = joinMessages(note, mapped.warning());
            row.seasons = toSeasonStates(mapped.seasons());
        } catch (TokenExpiredException e) {
            throw e;
        } catch (RuntimeException e) {
            log.warn("Import job {} row {} ({}) no se pudo resolver: {}",
                    job.getJobId(), source.lineNumber(), source.title(), e.getMessage());
            row.status = RowStatus.INVALID;
            row.message = "No se pudo resolver: " + messageOf(e);
            row.seasons = new ArrayList<>();
        }
    }

    private List<ImportJob.RowState> rowsWithStatus(ImportJob job, RowStatus status) {
        List<ImportJob.RowState> rows = new ArrayList<>();
        for (ImportJob.RowState row : job.rows()) {
            if (row.status == status) {
                rows.add(row);
            }
        }
        return rows;
    }

    private void sync(ImportJob job, List<ImportJob.RowState> rows) {
        try {
            job.setProgress(new ImportProgress(0, rows.size(), null));
            int done = 0;
            for (ImportJob.RowState row : rows) {
                if (job.isCancelRequested()) {
                    finishCancelled(job);
                    return;
                }
                applyActions(job, row);
                syncRow(job, row);
                job.setProgress(new ImportProgress(++done, rows.size(), row.source.title()));
            }
            job.setCounts(ImportCounts.of(job));
            job.setPhase(ImportPhase.DONE);
        } catch (TokenExpiredException e) {
            failExpired(job);
        } catch (MalThrottledException e) {
            fail(job, "MAL limitó las solicitudes", e);
        } catch (RuntimeException e) {
            fail(job, "Error inesperado durante la sincronización", e);
        }
    }

    private void applyActions(ImportJob job, ImportJob.RowState row) {
        Set<Integer> overwrite = job.overwriteMalIds();
        Set<Integer> excluded = job.excludedMalIds();
        for (ImportJob.SeasonState season : row.seasons) {
            if (!season.selected || excluded.contains(season.malId)) {
                season.action = SeasonAction.SKIP;
            } else if (season.existing == null) {
                season.action = SeasonAction.CREATE;
            } else {
                season.action = overwrite.contains(season.malId) ? SeasonAction.UPDATE : SeasonAction.KEEP;
            }
        }
    }

    private void syncRow(ImportJob job, ImportJob.RowState row) {
        for (ImportJob.SeasonState season : row.seasons) {
            if (season.action == SeasonAction.CREATE || season.action == SeasonAction.UPDATE) {
                patch(job, row, season);
            }
        }
        updateRowStatus(row);
    }

    private void patch(ImportJob job, ImportJob.RowState row, ImportJob.SeasonState season) {
        Integer episodes = season.episodes == null || season.episodes == 0 ? null : season.episodes;
        season.sent = true;
        try {
            call(job, "PATCH anime " + season.malId, () -> client.patchListStatus(
                    job.getToken(), season.malId, MAL_STATUS_COMPLETED, season.score, episodes));
            season.error = null;
        } catch (TokenExpiredException e) {
            season.error = TOKEN_EXPIRED;
            throw e;
        } catch (RuntimeException e) {
            season.error = messageOf(e);
            log.warn("Import job {} row {} anime {} falló: {}",
                    job.getJobId(), row.source.lineNumber(), season.malId, e.getMessage());
        }
    }

    private void updateRowStatus(ImportJob.RowState row) {
        int actionable = 0;
        int failed = 0;
        for (ImportJob.SeasonState season : row.seasons) {
            if (season.action == SeasonAction.CREATE || season.action == SeasonAction.UPDATE) {
                actionable++;
                if (season.error != null) {
                    failed++;
                }
            }
        }
        if (failed == 0) {
            row.status = RowStatus.SYNCED;
        } else if (failed == actionable) {
            row.status = RowStatus.FAILED;
        } else {
            row.status = RowStatus.PARTIAL;
        }
    }

    private void reResolve(ImportJob job, List<RowDecision> decisions, boolean syncAfter) {
        try {
            List<ImportJob.RowState> toSync = new ArrayList<>();
            for (RowDecision decision : decisions) {
                ImportJob.RowState row = job.rowByNumber(decision.rowNumber());
                if (row == null) {
                    log.warn("Import job {}: no existe la fila {}", job.getJobId(), decision.rowNumber());
                    continue;
                }
                reResolveRow(job, row, decision.malId());
                if (syncAfter && row.status == RowStatus.READY) {
                    toSync.add(row);
                }
            }
            if (!toSync.isEmpty()) {
                sync(job, toSync);
                return;
            }
            job.setCounts(ImportCounts.of(job));
            job.setPhase(syncAfter ? ImportPhase.DONE : ImportPhase.PREVIEW_READY);
        } catch (TokenExpiredException e) {
            failExpired(job);
        } catch (MalThrottledException e) {
            fail(job, "MAL limitó las solicitudes", e);
        } catch (RuntimeException e) {
            fail(job, "Error inesperado al resolver la fila", e);
        }
    }

    private void reResolveRow(ImportJob job, ImportJob.RowState row, int malId) {
        try {
            ImportAnimeDetail base = detail(job, malId);
            List<ImportSeason> chain = chainResolver.resolve(base, id -> detail(job, id));
            ScoreMapper.Result mapped = scoreMapper.map(row.source.scores(), chain, job.existing());
            row.status = RowStatus.READY;
            row.candidates = List.of(mapper.toCandidate(base));
            row.message = mapped.warning();
            row.seasons = toSeasonStates(mapped.seasons());
        } catch (TokenExpiredException e) {
            throw e;
        } catch (RuntimeException e) {
            row.status = RowStatus.INVALID;
            row.message = "No se pudo resolver: " + messageOf(e);
            row.seasons = new ArrayList<>();
        }
    }

    private void retryFailed(ImportJob job) {
        try {
            List<ImportJob.RowState> rows = new ArrayList<>();
            for (ImportJob.RowState row : job.rows()) {
                if (row.status == RowStatus.READY || row.status == RowStatus.PARTIAL || row.status == RowStatus.FAILED) {
                    rows.add(row);
                }
            }
            job.setProgress(new ImportProgress(0, rows.size(), null));
            int done = 0;
            for (ImportJob.RowState row : rows) {
                if (job.isCancelRequested()) {
                    finishCancelled(job);
                    return;
                }
                if (containsPendingAction(row)) {
                    applyActions(job, row);
                }
                for (ImportJob.SeasonState season : row.seasons) {
                    boolean alreadySynced = season.sent && season.error == null;
                    boolean actionable = season.action == SeasonAction.CREATE || season.action == SeasonAction.UPDATE;
                    if (!alreadySynced && actionable) {
                        patch(job, row, season);
                    }
                }
                updateRowStatus(row);
                job.setProgress(new ImportProgress(++done, rows.size(), row.source.title()));
            }
            job.setCounts(ImportCounts.of(job));
            job.setPhase(ImportPhase.DONE);
        } catch (TokenExpiredException e) {
            failExpired(job);
        } catch (MalThrottledException e) {
            fail(job, "MAL limitó las solicitudes", e);
        } catch (RuntimeException e) {
            fail(job, "Error inesperado al reintentar", e);
        }
    }

    private static boolean containsPendingAction(ImportJob.RowState row) {
        for (ImportJob.SeasonState season : row.seasons) {
            if (season.action == SeasonAction.PENDING) {
                return true;
            }
        }
        return false;
    }

    private List<ImportJob.SeasonState> toSeasonStates(List<SeasonPreview> seasons) {
        List<ImportJob.SeasonState> states = new ArrayList<>(seasons.size());
        for (SeasonPreview season : seasons) {
            states.add(new ImportJob.SeasonState(season));
        }
        return states;
    }

    private ImportAnimeDetail detail(ImportJob job, int malId) {
        ResponseAnimeDetailsRequest response = call(job, "detalle anime " + malId,
                () -> client.getAnimeDetails(job.getToken(), malId));
        if (response == null) {
            throw new IllegalStateException("MAL no devolvió el anime " + malId);
        }
        return mapper.toDetail(response);
    }

    private List<SearchItem> search(ImportJob job, String title) {
        ResponseAnimeSearchRequest response = call(job, "búsqueda «" + title + "»",
                () -> client.searchAnime(job.getToken(), title));
        return mapper.toSearchItems(response);
    }

    /**
     * Toda llamada a MAL: 1 req/s global, backoff en 403/504 (throttle de MAL) y un solo
     * reintento para errores transitorios. 401 corta el job entero.
     */
    private <T> T call(ImportJob job, String what, Supplier<T> action) {
        int throttles = 0;
        int transientRetries = 0;
        while (true) {
            rateLimiter.acquire();
            try {
                T value = action.get();
                job.resetThrottles();
                return value;
            } catch (WebClientResponseException e) {
                int code = e.getStatusCode().value();
                if (code == 401) {
                    throw new TokenExpiredException();
                }
                if (code == 403 || code == 504) {
                    if (job.incrementThrottles() > 3) {
                        throw new MalThrottledException();
                    }
                    if (throttles++ < 2) {
                        rateLimiter.throttle("HTTP " + code + " en " + what + " (job " + job.getJobId() + ")");
                        continue;
                    }
                }
                throw e;
            } catch (RuntimeException e) {
                if (transientRetries++ < 1) {
                    log.warn("Import job {}: reintentando {} ({})", job.getJobId(), what, e.getMessage());
                    continue;
                }
                throw e;
            }
        }
    }

    private void failExpired(ImportJob job) {
        log.warn("Import job {}: token de MAL expirado", job.getJobId());
        job.setError(TOKEN_EXPIRED);
        job.setPhase(ImportPhase.FAILED);
        job.setCounts(ImportCounts.of(job));
    }

    private void fail(ImportJob job, String context, RuntimeException e) {
        log.error("Import job {}: {}", job.getJobId(), context, e);
        job.setError(context + ": " + messageOf(e));
        job.setPhase(ImportPhase.FAILED);
        job.setCounts(ImportCounts.of(job));
    }

    private void finishCancelled(ImportJob job) {
        job.setCounts(ImportCounts.of(job));
        job.setPhase(ImportPhase.CANCELLED);
    }

    private ImportJob require(String jobId) {
        ImportJob job = store.get(jobId);
        if (job == null) {
            throw new IllegalArgumentException("Importación no encontrada o expirada");
        }
        return job;
    }

    private static String joinMessages(String first, String second) {
        if (first == null) {
            return second;
        }
        return second == null ? first : first + ". " + second;
    }

    private static String messageOf(Throwable e) {
        if (e instanceof WebClientResponseException web) {
            return "MAL respondió " + web.getStatusCode().value();
        }
        return e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage();
    }

    private static final class TokenExpiredException extends RuntimeException {
    }

    private static final class MalThrottledException extends RuntimeException {
        MalThrottledException() {
            super("esperá unos minutos y usá «Reintentar fallidos»");
        }
    }
}
