package com.graso.anitrack.importer.model;

public record ImportCounts(
        int rows,
        int seasonsToSync,
        int conflicts,
        int unresolved,
        int synced,
        int skipped,
        int failed
) {

    public static ImportCounts of(ImportJob job) {
        int rows = job.rowCount();
        int seasonsToSync = 0;
        int conflicts = 0;
        int unresolved = 0;
        int synced = 0;
        int skipped = 0;
        int failed = 0;
        for (ImportJob.RowState row : job.rows()) {
            if (row.status == RowStatus.AMBIGUOUS || row.status == RowStatus.NOT_FOUND) {
                unresolved++;
            }
            for (ImportJob.SeasonState season : row.seasons) {
                if (season.action == SeasonAction.CREATE || season.action == SeasonAction.UPDATE) {
                    seasonsToSync++;
                    if (season.sent && season.error == null) {
                        synced++;
                    }
                }
                if (season.action == SeasonAction.KEEP) {
                    skipped++;
                }
                if (season.existing != null) {
                    conflicts++;
                }
                if (season.error != null) {
                    failed++;
                }
            }
        }
        return new ImportCounts(rows, seasonsToSync, conflicts, unresolved, synced, skipped, failed);
    }
}
