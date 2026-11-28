package com.graso.anitrack.importer.model;

import com.graso.anitrack.importer.csv.CsvAnimeRow;
import com.graso.anitrack.importer.csv.CsvRowError;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Estado mutable de una importación. Lo mutan los hilos del executor de importación
 * (uno por job); los accesos desde el hilo HTTP pasan por estos métodos synchronized.
 */
public class ImportJob {

    private final String jobId;
    private String token;
    private final Instant createdAt = Instant.now();
    private final List<RowState> rows = new ArrayList<>();
    private final Set<Integer> overwriteMalIds = new HashSet<>();
    private final Set<Integer> excludedMalIds = new HashSet<>();
    private final Map<Integer, ExistingStatus> existingByMalId = new HashMap<>();
    private int consecutiveThrottles;
    private ImportPhase phase = ImportPhase.PARSING;
    private ImportProgress progress = new ImportProgress(0, 0, null);
    private ImportCounts counts = new ImportCounts(0, 0, 0, 0, 0, 0, 0);
    private String error;
    private boolean cancelRequested;

    public ImportJob(String jobId, String token) {
        this.jobId = jobId;
        this.token = token;
    }

    public String getJobId() {
        return jobId;
    }

    public synchronized String getToken() {
        return token;
    }

    public synchronized int incrementThrottles() {
        return ++consecutiveThrottles;
    }

    public synchronized void resetThrottles() {
        consecutiveThrottles = 0;
    }

    public synchronized void setToken(String token) {
        if (token != null && !token.isBlank()) {
            this.token = token;
        }
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public synchronized ImportPhase getPhase() {
        return phase;
    }

    public synchronized void setPhase(ImportPhase phase) {
        this.phase = phase;
    }

    public synchronized ImportProgress getProgress() {
        return progress;
    }

    public synchronized void setProgress(ImportProgress progress) {
        this.progress = progress;
    }

    public synchronized ImportCounts getCounts() {
        return counts;
    }

    public synchronized void setCounts(ImportCounts counts) {
        this.counts = counts;
    }

    public synchronized String getError() {
        return error;
    }

    public synchronized void setError(String error) {
        this.error = error;
    }

    public synchronized boolean isCancelRequested() {
        return cancelRequested;
    }

    public synchronized void requestCancel() {
        this.cancelRequested = true;
    }

    public synchronized List<RowState> rows() {
        return new ArrayList<>(rows);
    }

    public synchronized int rowCount() {
        return rows.size();
    }

    public synchronized void addRow(RowState row) {
        rows.add(row);
    }

    public synchronized RowState rowByNumber(int rowNumber) {
        for (RowState row : rows) {
            if (row.source.lineNumber() == rowNumber) {
                return row;
            }
        }
        return null;
    }

    public synchronized Set<Integer> overwriteMalIds() {
        return new HashSet<>(overwriteMalIds);
    }

    public synchronized void setOverwriteMalIds(Collection<Integer> malIds) {
        overwriteMalIds.clear();
        if (malIds != null) {
            overwriteMalIds.addAll(malIds);
        }
    }

    public synchronized Set<Integer> excludedMalIds() {
        return new HashSet<>(excludedMalIds);
    }

    public synchronized void setExcludedMalIds(Collection<Integer> malIds) {
        excludedMalIds.clear();
        if (malIds != null) {
            excludedMalIds.addAll(malIds);
        }
    }

    public synchronized Map<Integer, ExistingStatus> existing() {
        return new HashMap<>(existingByMalId);
    }

    public synchronized void setExisting(Map<Integer, ExistingStatus> existing) {
        existingByMalId.clear();
        existingByMalId.putAll(existing);
    }

    public synchronized List<PreviewRow> previewRows() {
        List<PreviewRow> view = new ArrayList<>(rows.size());
        for (RowState row : rows) {
            view.add(row.toPreview());
        }
        return view;
    }

    /** Struct mutable internal: solo lo toca el hilo que procesa el job. */
    public static final class RowState {
        public final CsvAnimeRow source;
        public RowStatus status;
        public String message;
        public List<Candidate> candidates = new ArrayList<>();
        public List<SeasonState> seasons = new ArrayList<>();

        public RowState(CsvAnimeRow source, RowStatus status, String message) {
            this.source = source;
            this.status = status;
            this.message = message;
        }

        public static RowState pending(CsvAnimeRow source) {
            return new RowState(source, RowStatus.PENDING, null);
        }

        public static RowState invalid(CsvRowError error) {
            return new RowState(
                    new CsvAnimeRow(error.lineNumber(), error.title(), null, List.of()),
                    RowStatus.INVALID,
                    error.message());
        }

        PreviewRow toPreview() {
            List<SeasonPreview> seasonViews = new ArrayList<>(seasons.size());
            for (SeasonState season : seasons) {
                seasonViews.add(season.toPreview());
            }
            return new PreviewRow(
                    source.lineNumber(),
                    source.title(),
                    status,
                    message,
                    List.copyOf(candidates),
                    seasonViews);
        }
    }

    /** Struct mutable internal, ver RowState. */
    public static final class SeasonState {
        public final int malId;
        public final String title;
        public final int order;
        public final Integer episodes;
        public final String image;
        public final int score;
        public final ExistingStatus existing;
        public boolean selected = true;
        public SeasonAction action = SeasonAction.PENDING;
        public String error;
        public boolean sent;

        public SeasonState(SeasonPreview preview) {
            this.malId = preview.malId();
            this.title = preview.title();
            this.order = preview.order();
            this.episodes = preview.episodes();
            this.image = preview.image();
            this.score = preview.score();
            this.selected = preview.selected();
            this.existing = preview.existing();
            this.action = preview.action();
            this.error = preview.error();
        }

        SeasonPreview toPreview() {
            return new SeasonPreview(malId, title, order, episodes, image, score, selected, existing, action, error);
        }
    }
}
