package com.graso.anitrack.importer.controller.dto;

import com.graso.anitrack.importer.model.ImportCounts;
import com.graso.anitrack.importer.model.ImportJob;
import com.graso.anitrack.importer.model.ImportPhase;
import com.graso.anitrack.importer.model.ImportProgress;
import com.graso.anitrack.importer.model.PreviewRow;

import java.time.Instant;
import java.util.List;

public record ImportJobResponse(
        String jobId,
        ImportPhase phase,
        ImportProgress progress,
        List<PreviewRow> rows,
        ImportCounts counts,
        String error,
        Instant createdAt
) {

    public static ImportJobResponse from(ImportJob job) {
        return new ImportJobResponse(
                job.getJobId(),
                job.getPhase(),
                job.getProgress(),
                job.previewRows(),
                job.getCounts(),
                job.getError(),
                job.getCreatedAt());
    }
}
